package net.swamphut.swampium.ui.element

import net.swamphut.swampium.ui.event.UIElementEvent

class UIPropagationController(override val target: UIElement) : UIElementEvent {
    override var isPropagating: Boolean = true
        private set

    override fun stopPropagation() {
        isPropagating = false
    }

    override var currentTarget = target


}