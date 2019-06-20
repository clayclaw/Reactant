package net.swamphut.swampium.ui.creation

import net.swamphut.swampium.ui.ViewInventoryContainerElement
import net.swamphut.swampium.ui.SwUIView

class SwUICreation(val view: SwUIView) : SwUIElementCreation<ViewInventoryContainerElement>(view.rootElement)

fun createUI(creating: SwUICreation.() -> Unit): SwUIView {
    return SwUICreation(SwUIView()).apply(creating).view
}
