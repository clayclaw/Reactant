package dev.reactant.reactant.ui.element.type.sizing

import dev.reactant.reactant.ui.editing.UIElementEditing
import dev.reactant.reactant.ui.element.UIElement

interface WidthResizableElement : UIElement {
    override var width: Int
}

interface WidthResizableElementEditing<out T : WidthResizableElement> : UIElementEditing<T> {
    @JvmDefault
    var width: Int
        get() = element.width
        set(value) {
            element.width = value
        }
}
