package dev.reactant.reactant.ui.event

import dev.reactant.reactant.ui.eventtarget.UIEventTarget

interface UIEvent {
    fun propagateTo(eventTarget: UIEventTarget<UIEvent>) {
        eventTarget.event.onNext(this)
    }
}
