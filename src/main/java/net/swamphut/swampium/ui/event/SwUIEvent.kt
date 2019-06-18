package net.swamphut.swampium.ui.event

import net.swamphut.swampium.ui.element.UIElement

open class SwUIEvent(val target: UIElement) : UIEvent {
    var currentTarget: UIElement = target
    private var stopPropagation = false

    override fun stopPropagation() {
        this.stopPropagation = true
    }

}