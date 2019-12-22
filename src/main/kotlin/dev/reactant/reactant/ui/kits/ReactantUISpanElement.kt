package dev.reactant.reactant.ui.kits

import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.ElementDisplay
import dev.reactant.reactant.ui.element.style.PositioningStylePropertyValue
import dev.reactant.reactant.ui.element.style.fitContent
import dev.reactant.reactant.ui.element.style.inline

@UIElementName("span")
open class ReactantUISpanElement(elementIdentifier: String = "span") : ReactantUIDivElement(elementIdentifier) {
    override fun edit() = ReactantUISpanElementEditing(this)

    override var width: PositioningStylePropertyValue = fitContent
    override var height: PositioningStylePropertyValue = fitContent

    override var display: ElementDisplay = inline
}

open class ReactantUISpanElementEditing<out T : ReactantUISpanElement>(element: T)
    : ReactantUIDivElementEditing<T>(element)

fun ReactantUIElementEditing<ReactantUIElement>.span(creation: ReactantUISpanElementEditing<ReactantUISpanElement>.() -> Unit) {
    element.children.add(ReactantUISpanElement().also { ReactantUISpanElementEditing(it).apply(creation) })
}
