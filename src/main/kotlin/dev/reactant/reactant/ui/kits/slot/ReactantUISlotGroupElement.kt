package dev.reactant.uikit.element.slot

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.kits.ReactantUIDivElement
import dev.reactant.reactant.ui.kits.ReactantUIDivElementEditing
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.inventory.ItemStack

@UIElementName("slotGroup")
open class ReactantUISlotGroupElement(allocatedSchedulerService: SchedulerService)
    : ReactantUIDivElement(allocatedSchedulerService, "slotGroup"), ItemStorageElement {

    private var _quickPutTarget: ItemStorage? = null
    override var quickPutTarget: ItemStorage?
        get() = _quickPutTarget ?: parent?.let { findQuickPutTarget(it) }
        set(value) = run { _quickPutTarget = value }

    private fun findQuickPutTarget(finding: UIElement): ItemStorage? = when (finding) {
        is ItemStorageElement -> finding.quickPutTarget
        else -> parent?.let { findQuickPutTarget(it) }
    }

    override fun putItems(items: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> {
        return children.mapNotNull { it as? ItemStorage }.fold(items) { previousResult, el -> el.putItems(previousResult, from) }
    }

    override fun testPutItems(items: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> {
        return children.mapNotNull { it as? ItemStorage }.fold(items) { previousResult, el -> el.testPutItems(previousResult, from) }
    }

    private fun takeItems(wantedItems: Map<Int, ItemStack>, isTest: Boolean, from: ItemStorage?): Map<Int, ItemStack> {
        val takenItems = hashMapOf<Int, ItemStack>()
        children.mapNotNull { it as? ItemStorage }
                .fold(wantedItems) { latestNeeded, el ->
                    val taken = (if (isTest) el.testTakeItems(latestNeeded, from) else el.takeItems(latestNeeded, from))
                            .map { (index, itemStack) ->
                                index to itemStack.also {
                                    it.amount = it.amount.coerceAtMost(latestNeeded[index]!!.amount - (takenItems[index]?.amount
                                            ?: 0))
                                }
                            }.toMap()
                    taken.forEach { (index, itemStack) ->
                        takenItems[index].let {
                            if (it != null) it.amount += itemStack.amount
                            else takenItems[index] = itemStack
                        }
                    }
                    latestNeeded.map { (index, itemStack) ->
                        index to itemStack.clone()
                                .also { it.amount -= (taken[index]?.amount ?: 0).coerceAtLeast(0) }
                    }.toMap()
                }
        return takenItems;
    }

    override fun takeItems(wantedItems: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = takeItems(wantedItems, false, from)
    override fun testTakeItems(wantedItems: Map<Int, ItemStack>, from: ItemStorage?): Map<Int, ItemStack> = takeItems(wantedItems, true, from)

    override fun iterator(): Iterator<ItemStack> = children.asSequence().mapNotNull { it as? ItemStorage }.flatten().iterator()
}


open class ReactantUISlotGroupElementEditing<out T : ReactantUISlotGroupElement>(element: T)
    : ReactantUIDivElementEditing<T>(element) {
    var quickPutTarget by MutablePropertyDelegate(element::quickPutTarget)
}

fun ReactantUIElementEditing<ReactantUIElement>.slotGroup(creation: ReactantUISlotGroupElementEditing<ReactantUISlotGroupElement>.() -> Unit) {
    element.children.add(ReactantUISlotGroupElement(element.allocatedSchedulerService)
            .also { ReactantUISlotGroupElementEditing(it).apply(creation) })
}
