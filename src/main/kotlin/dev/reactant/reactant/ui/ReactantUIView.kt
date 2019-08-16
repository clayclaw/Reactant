package dev.reactant.reactant.ui

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.element.UIElementChildren
import dev.reactant.reactant.ui.event.UIEvent
import dev.reactant.reactant.ui.rendering.ElementSlot
import dev.reactant.reactant.ui.rendering.RenderedItems
import io.reactivex.subjects.PublishSubject
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class ReactantUIView(override val scheduler: SchedulerService, private val showPlayerFunc: (ReactantUIView, Player) -> Unit, val height: Int) : UIView {

    override fun show(player: Player) = showPlayerFunc(this, player)

    override val event = PublishSubject.create<UIEvent>()

    var width = 9

    private var _inventory: Inventory? = null;
    override val inventory: Inventory
        get() {
            if (_inventory == null) {
                _inventory = Bukkit.createInventory(null, width * height, "Test")
            }
            return _inventory!!
        }

    override val rootElement = ViewInventoryContainerElement(this)

    override val children: UIElementChildren = LinkedHashSet<UIElement>(setOf(rootElement))

    private var lastRenderResult: RenderedItems? = null
        private set

    override fun render() {
        lastRenderResult = rootElement.render(rootElement.width, rootElement.height)
        scheduleUpdate()
    }

    override fun getElementAt(x: Int, y: Int): UIElement? {
        if (lastRenderResult == null) throw IllegalStateException("UI never be rendered")
        return lastRenderResult!!.items[x to y]?.element
    }

    private fun scheduleUpdate() {
        scheduler.next().subscribe(this::updateView)
    }

    private fun updateView() {
        lastRenderResult!!.items
                .filter { it.value != ElementSlot.EMPTY }
                .map { (pos, slot) -> (pos.second * 9 + pos.first) to slot.itemStack }
                // filter out the slots which have different ItemStack
                .filter { inventory.getItem(it.first)?.equals(it.second)?.not() ?: true }
                .forEach { inventory.setItem(it.first, it.second) }
    }
}
