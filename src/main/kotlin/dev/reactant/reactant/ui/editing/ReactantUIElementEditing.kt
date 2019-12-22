package dev.reactant.reactant.ui.editing

import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementClassList
import dev.reactant.reactant.ui.element.style.UIElementStyle
import dev.reactant.reactant.ui.element.style.UIElementStyleEditing
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate

open class ReactantUIElementEditing<out T : ReactantUIElement>(final override val element: T)
    : UIElementStyleEditing by element, UIElementEditing<T> {
    init {
        element.view?.render()
    }

    override var id by MutablePropertyDelegate(element::id)

    override var classList: UIElementClassList
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


    val actual = UIElementStyle.Companion::actual
    val percentage = UIElementStyle.Companion::percentage
    val auto = UIElementStyle.auto
    val fitContent = UIElementStyle.fitContent
    val fillParent = UIElementStyle.fillParent

    val fixed = UIElementStyle.fixed
    val static = UIElementStyle.static
    val absolute = UIElementStyle.absolute
    val relative = UIElementStyle.relative

    val block = UIElementStyle.block
    val inline = UIElementStyle.inline
}
