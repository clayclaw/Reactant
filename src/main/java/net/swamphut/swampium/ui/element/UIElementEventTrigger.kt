package net.swamphut.swampium.ui.element

import io.reactivex.subjects.Subject
import net.swamphut.swampium.ui.event.UIElementEvent

interface UIElementEventTrigger {
    val event: Subject<UIElementEvent>
}