package net.swamphut.swampium.ui.element.type.sizing

import net.swamphut.swampium.ui.editing.UIElementEditing
import net.swamphut.swampium.ui.element.UIElement

interface WidthResizableElement : UIElement {
    override var width: Int
}

interface WidthResizableElementEditing<T : WidthResizableElement> : UIElementEditing<T> {
    @JvmDefault
    var width: Int
        get() = element.width
        set(value) {
            element.width = value
        }
}
