package dev.reactant.uikit.element.slot

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.UIView
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.editing.event
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.event.UIElementEvent
import dev.reactant.reactant.ui.event.interact.UIClickEvent
import dev.reactant.reactant.ui.event.interact.UIDragEvent
import dev.reactant.reactant.ui.event.interact.element.UIElementClickEvent
import dev.reactant.reactant.ui.event.inventory.UICloseEvent
import dev.reactant.reactant.ui.kits.ReactantUIItemElement
import dev.reactant.reactant.ui.kits.ReactantUIItemElementEditing
import dev.reactant.reactant.utils.content.item.itemStackOf
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import io.reactivex.Observable
import org.bukkit.Material.AIR
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.min

@UIElementName("slot")
open class ReactantUISlotElement(allocatedSchedulerService: SchedulerService)
    : ReactantUIItemElement(allocatedSchedulerService), ItemStorageElement {
    private fun hotbarSwap(player: Player, hotbar: Int) {
        val hotbarItem = player.inventory.getItem(hotbar)
        ReactantUISlotElementSwapHotbarItemEvent(this, player, hotbar, hotbarItem, displayItem, PlayerItemStorage(player)).let {
            event.onNext(it)
            if (!it.isCancelled) {
                // todo: the handler should take to ui view level for future extend, e.g. using packet-fake bag ui
                val tmp = hotbarItem
                player.inventory.setItem(hotbar, displayItem)
                displayItem = tmp ?: itemStackOf(AIR)
                pushUpdatedEvent()
            }
        }
    }

    private fun pushUpdatedEvent() {
        event.onNext(ReactantUISlotUpdatedEvent(this))
    }

    private fun takeItem(player: Player, amount: Int) {
        if (displayItem.type != AIR && player.itemOnCursor.amount == 0) {
            val takingItem = displayItem.clone().also { it.amount = amount }
            ReactantUISlotTakeItemEvent(this, takingItem, PlayerItemStorage(player), false).let {
                event.onNext(it)
                if (!it.isCancelled) {
                    assert(takingItem.type == displayItem.type && takingItem.amount <= displayItem.amount)
                    displayItem = displayItem.clone().also { it.amount -= takingItem.amount }
                    if (displayItem.amount == 0) displayItem = itemStackOf(AIR)
                    player.setItemOnCursor(takingItem)
                    pushUpdatedEvent()
                }
            }
        }
    }

    private fun putItem(player: Player, amount: Int) {
        if (player.itemOnCursor.amount != 0 && (displayItem.type == AIR || displayItem.isSimilar(player.itemOnCursor))) {
            val putting = player.itemOnCursor.clone().also { it.amount = amount }
            putItem(putting, PlayerItemStorage(player)).let { returned ->
                // it is not return other item when player putting the item
                assert(returned == null || returned.isSimilar(putting))

                putting.amount -= (returned?.amount ?: 0)
                when {
                    player.itemOnCursor.amount > putting.amount -> player.itemOnCursor.amount -= putting.amount
                    else -> player.setItemOnCursor(null)
                }
            }
        }
    }

    private fun swapItem(player: Player) {
        if (putAmountLimit(player.itemOnCursor) >= player.itemOnCursor.amount && takeAmountLimit(displayItem.clone()) >= displayItem.amount) {
            ReactantUISlotElementSwapCursorItemEvent(this, player, player.itemOnCursor, displayItem, PlayerItemStorage(player)).let {
                event.onNext(it)
                if (!it.isCancelled) {
                    val tmp = player.itemOnCursor
                    player.setItemOnCursor(displayItem)
                    displayItem = tmp
                    pushUpdatedEvent()
                }
            }
        }
    }

    fun quickPut(playerItemStorage: PlayerItemStorage) {
        (quickPutTarget ?: playerItemStorage).let { quickPutIS ->
            quickPutIS.testPutItem(displayItem, this).let { returned ->
                val puttingItem = displayItem.clone().also { it.amount - (returned?.amount ?: 0) }
                ReactantUISlotTakeItemEvent(this, puttingItem, quickPutIS, false).let {
                    event.onNext(it)
                    if (!it.isCancelled) {
                        assert(puttingItem.type == displayItem.type && puttingItem.amount <= displayItem.amount)
                        quickPutIS.putItem(puttingItem, this).let { actualReturned ->
                            val actualPutting = puttingItem.amount - (actualReturned?.amount ?: 0)
                            displayItem.amount -= actualPutting
                            if (displayItem.amount == 0) displayItem = itemStackOf(AIR)
                            pushUpdatedEvent()
                        }
                    }
                }
            }
        }
    }

    override val elementIdentifier = "slot"

    override fun edit() = ReactantUISlotElementEditing(this)

    private fun findQuickPutTarget(finding: UIElement): ItemStorage? = when (finding) {
        is ItemStorageElement -> finding.quickPutTarget
        else -> finding.parent?.let { findQuickPutTarget(it) }
    }

    private var _quickPutTarget: ItemStorage? = null
    override var quickPutTarget: ItemStorage?
        get() = _quickPutTarget ?: parent?.let { findQuickPutTarget(it) }
        set(value) = run { _quickPutTarget = value }

    override var slotIndex: Int = 0

    /**
     * Use to limit how many item can player put
     * Input with the ItemStack that player trying to put
     * Return the maximum amount of this type of item the slot can store, included original item in this slot
     * Return 0 mean input from player is disabled, 1 mean this slot only can store 1 item
     */
    var putAmountLimit: (ItemStack) -> Int = { itemStack -> itemStack.type.maxStackSize }

    /**
     * Use to limit how many item can player take
     * Input with the ItemStack that player trying to take
     * Return the maximum amount of this type of item that player can take
     * Return 0 mean output to player is disabled, 1 mean player need to take the item 64 times to take full stack of item
     * Normally this value should be based on how many item in this slot
     */
    var takeAmountLimit: (ItemStack) -> Int = { itemStack -> itemStack.type.maxStackSize }


    private fun putItems(items: Map<Int, ItemStack>, from: ItemStorage?, isTest: Boolean): Map<Int, ItemStack> {
        var newDisplayItem = displayItem.clone()
        return items.mapNotNull { (i, input) ->

            // the calculated amount that this slot can receive
            val putting: ItemStack = input.clone()
            putting.amount = putting.amount
                    .coerceAtMost(putAmountLimit(putting) - displayItem.let { if (it.type.isAir) 0 else it.amount })
                    .coerceAtLeast(0)
            ReactantCore.logger.info("To ${putting.amount}")

            when {
                // accept full stack item
                newDisplayItem.type.isAir -> Unit
                // calculate accepting amount
                newDisplayItem.isSimilar(putting) ->
                    putting.amount = min(newDisplayItem.maxStackSize - newDisplayItem.amount, input.amount)
                // put nothing
                else -> putting.amount = 0
            }

            // fire event
            if (putting.amount != 0) {
                ReactantUISlotPutItemEvent(this, putting, from, isTest).let { putItemEvent ->
                    event.onNext(putItemEvent)

                    if (!putItemEvent.isCancelled) {
                        when (newDisplayItem.type) {
                            AIR -> newDisplayItem = putting
                            else -> newDisplayItem.amount += putting.amount
                        }
                    }
                }
            }

            when (putting.amount) {
                // if can't put anything, return all
                0 -> i to input.clone()
                // if put everything, return nothing
                input.amount -> null
                // return partly (at most its original size)
                else -> i to input.clone().also { it.amount -= putting.amount.coerceAtMost(it.amount) }
            }
        }.toMap().also {
            if (!isTest) {
                this.displayItem = newDisplayItem
                pushUpdatedEvent()
            }
        }
    }

    override fun putItems(items: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = putItems(items, from, false)

    override fun testPutItems(items: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = putItems(items, from, true)

    private fun takeItems(wantedItems: Map<Int, ItemStack>, from: ItemStorage?, isTest: Boolean): Map<Int, ItemStack> {
        var newDisplayItem = displayItem.clone()
        return wantedItems.mapNotNull { (i, wanted) ->

            val taking: ItemStack = wanted.clone()
            taking.amount = taking.amount.coerceAtMost(takeAmountLimit(taking)).coerceAtLeast(0)
            when {
                // if similar, fulfill the request as possible
                !newDisplayItem.type.isAir && newDisplayItem.isSimilar(taking) -> taking.amount = min(newDisplayItem.amount, taking.amount)
                // cannot give anything
                else -> taking.amount = 0
            }

            // fire event
            if (taking.amount != 0) {
                ReactantUISlotTakeItemEvent(this, taking, from, isTest).let { takeItemEvent ->
                    event.onNext(takeItemEvent)

                    if (!takeItemEvent.isCancelled) {
                        assert(taking.type == displayItem.type && taking.amount <= displayItem.amount)
                        newDisplayItem.amount -= taking.amount
                        if (newDisplayItem.amount == 0) newDisplayItem = itemStackOf(AIR)
                    }
                }
            }

            when (taking.amount) {
                // if can't take anything, return nothing
                0 -> null
                // if take something, return the item
                else -> i to taking
            }
        }.toMap().also {
            if (!isTest) {
                this.displayItem = newDisplayItem
                pushUpdatedEvent()
            }
        }
    }

    override fun takeItems(wantedItems: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = takeItems(wantedItems, from, false)

    override fun testTakeItems(wantedItems: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = takeItems(wantedItems, from, true)

    override fun iterator(): Iterator<ItemStack> = displayItem
            .let { if (it.type.isAir) emptySequence() else sequenceOf(it) }.iterator()

    init {
        edit().apply {
            event<UIElementClickEvent>().subscribe {
                it.isCancelled = true
                it.stopPropagation()

                scheduler.next().subscribe {
                    val player = it.bukkitEvent.whoClicked as Player
                    val slotAmount = displayItem.amount
                    when (it.bukkitEvent.action) {
                        MOVE_TO_OTHER_INVENTORY -> if (slotAmount != 0) quickPut(PlayerItemStorage(it.bukkitEvent.whoClicked as Player))
                        PICKUP_ALL -> takeItem(player, slotAmount)
                        PICKUP_SOME -> takeItem(player, slotAmount)
                        PICKUP_HALF -> takeItem(player, ceil(slotAmount / 2.0).toInt())
                        PICKUP_ONE -> takeItem(player, min(slotAmount, 1))
                        PLACE_ALL -> putItem(player, player.itemOnCursor.amount)
                        PLACE_SOME -> putItem(player, player.itemOnCursor.amount)
                        PLACE_ONE -> putItem(player, min(player.itemOnCursor.amount, 1))
                        SWAP_WITH_CURSOR -> when {
                            slotAmount != 0 -> swapItem(player)
                            it.bukkitEvent.isLeftClick -> putItem(player, player.itemOnCursor.amount)
                            it.bukkitEvent.isRightClick -> putItem(player, min(player.itemOnCursor.amount, 1))
                        }
                        HOTBAR_SWAP -> hotbarSwap(player, it.bukkitEvent.hotbarButton)
                        else -> run { }
                    }
                }
            }
        }
    }


}

fun UIView.setAsSlotView(playerInventoryQuickPutTarget: ItemStorage, dropCursorWhenClose: Boolean = true) {
    this.event.ofType(UIDragEvent::class.java).subscribe { it.isCancelled = true }
    this.event.ofType(UIClickEvent::class.java).filter { it is UIElementEvent }.subscribe { it.isCancelled = true }
    this.event.ofType(UIClickEvent::class.java).filter { it !is UIElementEvent }.subscribe {
        if (it.bukkitEvent.action == MOVE_TO_OTHER_INVENTORY) {
            it.isCancelled = true
            it.bukkitEvent.currentItem?.let { movingItem ->
                playerInventoryQuickPutTarget.putItem(movingItem, PlayerItemStorage(it.bukkitEvent.whoClicked as Player)).let { returned ->
                    it.bukkitEvent.whoClicked.inventory.setItem(it.bukkitEvent.slot, returned)
                }
            }
        }
    }
    if (dropCursorWhenClose) this.event.ofType(UICloseEvent::class.java).subscribe {
        if (!it.player.itemOnCursor.type.isAir) it.player.world.dropItem(it.player.location, it.player.itemOnCursor)
    }
}

open class ReactantUISlotElementEditing<out T : ReactantUISlotElement>(element: T)
    : ReactantUIItemElementEditing<T>(element) {
    var quickPutTarget by MutablePropertyDelegate(element::quickPutTarget)
    var slotIndex by MutablePropertyDelegate(element::slotIndex)
    var putAmountLimit by MutablePropertyDelegate(element::putAmountLimit)
    var takeAmountLimit by MutablePropertyDelegate(element::takeAmountLimit)

    override val onClick: Observable<UIElementClickEvent>
        get() = super.onClick

    val onSlotPut get() = event<ReactantUISlotPutItemEvent>()
    val onSlotTake get() = event<ReactantUISlotTakeItemEvent>()
    val onSlotUpdate get() = event<ReactantUISlotUpdatedEvent>()
}

fun ReactantUIElementEditing<ReactantUIElement>.slot(creation: ReactantUISlotElementEditing<ReactantUISlotElement>.() -> Unit) {
    element.children.add(ReactantUISlotElement(element.allocatedSchedulerService)
            .also { ReactantUISlotElementEditing(it).apply(creation) })
}


