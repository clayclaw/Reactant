package net.swamphut.swampium.ui.event

import net.swamphut.swampium.ui.eventtarget.UIEventTarget

interface UIEvent {
    fun propagateTo(eventTarget: UIEventTarget<UIEvent>) {
        eventTarget.event.onNext(this)
    }
}
