package net.swamphut.swampium.ui.creation

import net.swamphut.swampium.ui.SwUIView
import net.swamphut.swampium.ui.ViewInventoryContainerElement

class SwUICreation(val view: SwUIView) : SwUIElementCreation<ViewInventoryContainerElement>(view.rootElement) {
    fun view(action: SwUIView.() -> Unit) {
        view.apply(action)
    }
}

