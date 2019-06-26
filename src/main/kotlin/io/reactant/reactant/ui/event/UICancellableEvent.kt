package io.reactant.reactant.ui.event

interface UICancellableEvent : UIEvent {
    var isCancelled: Boolean
}