package net.swamphut.swampium.ui.kits

import net.swamphut.swampium.ui.creation.SwUIElementCreation
import net.swamphut.swampium.ui.element.ElementDisplay
import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.element.type.sizing.ResizableElement
import net.swamphut.swampium.ui.element.type.sizing.ResizableElementsCreation
import net.swamphut.swampium.ui.kits.container.SwUIContainerElement
import net.swamphut.swampium.ui.kits.container.SwUIContainerElementCreation
import net.swamphut.swampium.utils.content.item.createItemStack
import net.swamphut.swampium.utils.delegation.MutablePropertyDelegate
import org.bukkit.inventory.ItemStack

class SwUIDivElement : SwUIContainerElement("div"), ResizableElement {
    override var height: Int = UIElement.WRAP_CONTENT
    override var width: Int = UIElement.MATCH_PARENT
    override var display = ElementDisplay.BLOCK

    var fillPattern: (relativeX: Int, relativeY: Int) -> ItemStack = { _, _ -> createItemStack() }

    override fun getBackgroundItemStack(x: Int, y: Int): ItemStack = fillPattern(x, y)
}

open class SwUIDivElementCreation(element: SwUIDivElement)
    : SwUIContainerElementCreation<SwUIDivElement>(element), ResizableElementsCreation<SwUIDivElement> {
    var overflowHidden by MutablePropertyDelegate(element::overflowHidden)
    var fillPattern by MutablePropertyDelegate(element::fillPattern)
    fun fill(itemStack: ItemStack) {
        fillPattern = { _, _ -> itemStack.clone() }
    }
}

fun SwUIElementCreation<out UIElement>.div(creation: SwUIDivElementCreation.() -> Unit) {
    element.children.add(SwUIDivElement().also { SwUIDivElementCreation(it).apply(creation) })
}
