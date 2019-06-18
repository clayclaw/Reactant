package net.swamphut.swampium.ui.kits

import net.swamphut.swampium.ui.creation.SwUIElementCreation
import net.swamphut.swampium.ui.element.SwUIElement
import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.element.type.sizing.ResizableElement
import net.swamphut.swampium.ui.element.type.sizing.ResizableElementsCreation

class SwUIDivElement : SwUIContainerElement("div"), ResizableElement {
    override var height: Int = 1
    override var width: Int = ResizableElement.MATCH_PARENT
}

open class SwUIDivElementCreation(element: SwUIDivElement)
    : SwUIElementCreation<SwUIDivElement>(element), ResizableElementsCreation<SwUIDivElement>

fun SwUIElementCreation<out UIElement>.div(creation: SwUIDivElementCreation.() -> Unit) {
    element.children.add(SwUIDivElement().also { SwUIDivElementCreation(it).apply(creation) })
}
