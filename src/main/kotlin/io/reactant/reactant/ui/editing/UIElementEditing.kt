package io.reactant.reactant.ui.editing

import io.reactant.reactant.ui.element.ElementDisplay
import io.reactant.reactant.ui.element.UIElement
import io.reactant.reactant.ui.element.UIElementAttributes
import io.reactant.reactant.ui.element.UIElementClassList
import io.reactant.reactant.ui.event.interact.element.UIElementClickEvent
import io.reactivex.Observable

interface UIElementEditing<out T : UIElement> {
    val element: T;
    var id: String?
    var classList: UIElementClassList
    var attributes: UIElementAttributes
    var display: ElementDisplay

    @JvmDefault
    val click: Observable<UIElementClickEvent>
        get() = element.event.filter { it is UIElementClickEvent }.map { it as UIElementClickEvent }
}
