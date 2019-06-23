package net.swamphut.swampium.ui.event

import net.swamphut.swampium.ui.ViewInventoryContainerElement
import net.swamphut.swampium.ui.element.UIElement

/**
 * The event which is happening on an element
 */
abstract class UIElementEvent(val target: UIElement) : UIEvent {

    var isPropagating: Boolean = true
        private set

    fun stopPropagation() {
        isPropagating = false
    }

    fun propagateTo(uiElement: UIElement) {
        uiElement.event.onNext(this)
        if (!isPropagating) return
        when {
            uiElement is ViewInventoryContainerElement -> propagateTo(uiElement.view)
            uiElement.parent != null -> propagateTo(uiElement.parent!!)
        }
    }

}
