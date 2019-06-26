package io.reactant.reactant.ui.event

import io.reactant.reactant.ui.eventtarget.UIEventTarget

interface UIEvent {
    fun propagateTo(eventTarget: UIEventTarget<UIEvent>) {
        eventTarget.event.onNext(this)
    }
}
