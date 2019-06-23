package net.swamphut.swampium.ui.element.type.sizing

import net.swamphut.swampium.ui.editing.UIElementEditing
import net.swamphut.swampium.ui.element.UIElement

/**
 * Add ability to change the height of this element
 */
interface HeightResizableElement : UIElement {
    override var height: Int
}

interface HeightResizableElementEditing<T : HeightResizableElement> : UIElementEditing<T> {
    @JvmDefault
    var height: Int
        get() = element.height
        set(value) {
            element.height = value
        }
}
