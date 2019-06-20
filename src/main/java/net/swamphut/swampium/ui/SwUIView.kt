package net.swamphut.swampium.ui

import io.reactivex.subjects.Subject
import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.event.UIEvent
import net.swamphut.swampium.ui.rendering.RenderedItems
import org.bukkit.Bukkit

class SwUIView() : UIView {
    override val event: Subject<UIEvent>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override val inventory = Bukkit.createInventory(null, 9, "Test")

    override val rootElement = ViewInventoryContainerElement(this)

    override var lastRenderResult: RenderedItems? = null
        private set

    override fun render() {
        lastRenderResult = rootElement.render()
        lastRenderResult!!.items
                .map { (pos, slot) -> (pos.second * 9 + pos.first) to slot.itemStack }
                // filter out the slots which have different ItemStack
                .filter { inventory.getItem(it.first)?.equals(it.second)?.not() ?: true }
                .forEach { inventory.setItem(it.first, it.second) }
    }

    override fun getElementAt(x: Int, y: Int): UIElement? {
        if (lastRenderResult == null) throw IllegalStateException("UI never be rendered")
        return lastRenderResult!!.items[x to y]?.element
    }
}
