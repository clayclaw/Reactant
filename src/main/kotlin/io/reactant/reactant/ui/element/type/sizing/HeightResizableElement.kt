package io.reactant.reactant.ui.element.type.sizing

import io.reactant.reactant.ui.editing.UIElementEditing
import io.reactant.reactant.ui.element.UIElement

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
