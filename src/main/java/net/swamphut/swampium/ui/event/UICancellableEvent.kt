package net.swamphut.swampium.ui.event

interface UICancellableEvent : UIEvent {
    var isCancelled: Boolean
}