package net.swamphut.swampium.ui.element.type.sizing

import net.swamphut.swampium.ui.creation.UIElementCreation
import net.swamphut.swampium.ui.element.UIElement

interface WidthResizableElement : UIElement {
    override var width: Int
}

interface WidthResizableElementCreation<T : WidthResizableElement> : UIElementCreation<T> {
    @JvmDefault
    var width: Int
        get() = element.width
        set(value) {
            element.width = value
        }
}
