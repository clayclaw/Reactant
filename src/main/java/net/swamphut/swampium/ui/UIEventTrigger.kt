package net.swamphut.swampium.ui

import io.reactivex.subjects.Subject
import net.swamphut.swampium.ui.event.UIEvent

interface UIEventTrigger {
    val event: Subject<UIEvent>
}