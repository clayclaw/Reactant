package dev.reactant.reactant.ui.kits

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.fitContent
import dev.reactant.reactant.ui.element.style.inline

@UIElementName("span")
open class ReactantUISpanElement(allocatedSchedulerService: SchedulerService) : ReactantUIDivElement(allocatedSchedulerService) {
    init {
        width = fitContent
        height = fitContent
        display = inline
    }

    override fun edit() = ReactantUISpanElementEditing(this)
}

open class ReactantUISpanElementEditing<out T : ReactantUISpanElement>(element: T)
    : ReactantUIDivElementEditing<T>(element)

fun ReactantUIElementEditing<ReactantUIElement>.span(creation: ReactantUISpanElementEditing<ReactantUISpanElement>.() -> Unit) {
    element.children.add(ReactantUISpanElement(element.allocatedSchedulerService).also { it.edit().apply(creation) })
}
