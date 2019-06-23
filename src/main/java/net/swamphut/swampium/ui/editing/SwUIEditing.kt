package net.swamphut.swampium.ui.editing

import net.swamphut.swampium.ui.SwUIView
import net.swamphut.swampium.ui.ViewInventoryContainerElement
import net.swamphut.swampium.ui.kits.container.SwUIContainerElementEditing

class SwUIEditing(val view: SwUIView) : SwUIContainerElementEditing<ViewInventoryContainerElement>(view.rootElement) {
    fun view(action: SwUIView.() -> Unit) {
        view.apply(action)
    }
}

