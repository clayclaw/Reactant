package net.swamphut.swampium.ui

import io.reactivex.Observable
import io.reactivex.subjects.Subject
import net.swamphut.swampium.ui.event.UIEvent
import net.swamphut.swampium.ui.event.interact.UIClickEvent
import net.swamphut.swampium.ui.event.inventory.UICloseEvent

interface UIEventTrigger {
    val event: Subject<UIEvent>
    @JvmDefault
    val click: Observable<UIClickEvent>
        get() = event.filter { it is UIClickEvent }.map { it as UIClickEvent }

    val close: Observable<UICloseEvent>
        get() = event.filter { it is UICloseEvent }.map { it as UICloseEvent }


}
