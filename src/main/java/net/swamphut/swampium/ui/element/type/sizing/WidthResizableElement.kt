package net.swamphut.swampium.ui.element.type.sizing

import net.swamphut.swampium.ui.creation.UIElementCreation
import net.swamphut.swampium.ui.element.UIElement

interface WidthResizableElement : UIElement {
    var width: Int

    companion object {
        const val MATCH_PARENT = -1
    }
}

interface WidthResizableElementCreation<T : WidthResizableElement> : UIElementCreation<T> {
    @JvmDefault
    var width: Int
        get() = element.width
        set(value) {
            element.width = value
        }
}
