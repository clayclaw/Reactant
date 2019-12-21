package dev.reactant.reactant.ui.editing

import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.element.UIElementAttributes
import dev.reactant.reactant.ui.element.UIElementClassList
import dev.reactant.reactant.ui.element.style.UIElementStyleEditing
import dev.reactant.reactant.ui.event.UIElementEvent
import dev.reactant.reactant.ui.event.interact.element.UIElementClickEvent
import dev.reactant.reactant.ui.event.interact.element.UIElementDragEvent
import io.reactivex.Observable

interface UIElementEditing<out T : UIElement> : UIElementStyleEditing {
    val element: T;
    var id: String?
    var classList: UIElementClassList
    var attributes: UIElementAttributes

    fun attributes(vararg modifiers: Pair<String, String>) {
        attributes.putAll(modifiers)
    }

    @JvmDefault
    val event
        get() = element.event;

    @JvmDefault
    val click: Observable<UIElementClickEvent>
        get() = event.filter { it is UIElementClickEvent }.map { it as UIElementClickEvent }

    @JvmDefault
    val drag: Observable<UIElementDragEvent>
        get() = event.filter { it is UIElementDragEvent }.map { it as UIElementDragEvent }

}

inline fun <reified T : UIElementEvent> UIElementEditing<UIElement>.event(): Observable<T> {
    return event.filter { it is T }.map { it as? T }
}
