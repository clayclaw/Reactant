package io.reactant.reactant.ui.element.type.sizing

import io.reactant.reactant.ui.editing.UIElementEditing
import io.reactant.reactant.ui.element.UIElement

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
