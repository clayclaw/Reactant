package net.swamphut.swampium.ui.creation

import net.swamphut.swampium.ui.element.ElementDisplay
import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.utils.delegation.MutablePropertyDelegate

open class SwUIElementCreation<T : UIElement>(final override val element: T) : UIElementCreation<T> {
    override var display: ElementDisplay by MutablePropertyDelegate(element::display)
    override var id by MutablePropertyDelegate(element::id)

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