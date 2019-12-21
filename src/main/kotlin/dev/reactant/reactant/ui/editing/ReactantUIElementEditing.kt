package dev.reactant.reactant.ui.editing

import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementClassList
import dev.reactant.reactant.ui.element.style.UIElementStyle
import dev.reactant.reactant.ui.element.style.UIElementStyleEditing
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate

open class ReactantUIElementEditing<out T : ReactantUIElement>(final override val element: T) : UIElementStyleEditing by element, UIElementEditing<T> {
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


    val actual get() = UIElementStyle.Companion::actual
    val percentage get() = UIElementStyle.Companion::percentage
    val auto get() = UIElementStyle.auto
    val fitContent get() = UIElementStyle.fitContent
    val fillParent get() = UIElementStyle.fillParent

    val fixed get() = UIElementStyle.fixed
    val static get() = UIElementStyle.static
    val absolute get() = UIElementStyle.absolute
    val relative get() = UIElementStyle.relative

    val block get() = UIElementStyle.block
    val inline get() = UIElementStyle.inline
}
