package net.swamphut.swampium.ui.event

import net.swamphut.swampium.ui.ViewInventoryContainerElement
import net.swamphut.swampium.ui.element.UIElement

/**
 * The event which is happening on an element
 */
interface UIElementEvent : UIEvent {
    val target: UIElement

    val isPropagating: Boolean
    fun stopPropagation()

    fun propagateTo(uiElement: UIElement) {
        uiElement.event.onNext(this)
        if (!isPropagating) return
        when {
            uiElement is ViewInventoryContainerElement -> propagateTo(uiElement.swUIView)
            uiElement.parent != null -> propagateTo(uiElement.parent!!)
        }
    }

}