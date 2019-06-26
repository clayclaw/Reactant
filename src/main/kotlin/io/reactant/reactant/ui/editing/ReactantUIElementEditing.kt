package io.reactant.reactant.ui.editing

import io.reactant.reactant.ui.element.ElementDisplay
import io.reactant.reactant.ui.element.UIElement
import io.reactant.reactant.utils.delegation.MutablePropertyDelegate

open class ReactantUIElementEditing<T : UIElement>(final override val element: T) : UIElementEditing<T> {
    override var display: ElementDisplay by MutablePropertyDelegate(element::display)
    override var id by MutablePropertyDelegate(element::id)

    var marginTop: Int by MutablePropertyDelegate(element::marginTop)
    var marginRight: Int by MutablePropertyDelegate(element::marginRight)
    var marginBottom: Int by MutablePropertyDelegate(element::marginBottom)
    var marginLeft: Int by MutablePropertyDelegate(element::marginLeft)
    var margin: List<Int> by MutablePropertyDelegate(element::margin)

    open fun margin(vararg margin: Int) {
        this.margin = margin.toList()
    }

    override var classList
        get() = element.classList
        set(value) {
            element.classList.clear()
            element.classList.addAll(value)
        }

    override var attributes
        get() = element.attributes
        set(value) {
            element.attributes.clear()
            element.attributes.putAll(value)
        }
}
