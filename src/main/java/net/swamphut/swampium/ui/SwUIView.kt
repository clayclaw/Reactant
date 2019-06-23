package net.swamphut.swampium.ui

import io.reactivex.subjects.PublishSubject
import net.swamphut.swampium.service.spec.server.SchedulerService
import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.element.UIElementChildren
import net.swamphut.swampium.ui.event.UIEvent
import net.swamphut.swampium.ui.rendering.ElementSlot
import net.swamphut.swampium.ui.rendering.RenderedItems
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class SwUIView(override val scheduler: SchedulerService, private val showPlayerFunc: (SwUIView, Player) -> Unit) : UIView {

    override fun show(player: Player) = showPlayerFunc(this, player)

    override val event = PublishSubject.create<UIEvent>()

    override val inventory = Bukkit.createInventory(null, 9 * 6, "Test")

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

    init {
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
