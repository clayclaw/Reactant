package net.swamphut.swampium.ui.event

import net.swamphut.swampium.ui.UIEventTrigger

interface UIEvent {
    fun propagateTo(uiEventTrigger: UIEventTrigger) {
        uiEventTrigger.event.onNext(this)
    }
}