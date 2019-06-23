package net.swamphut.swampium.ui.eventtarget

import io.reactivex.Observable
import net.swamphut.swampium.ui.event.UIEvent
import net.swamphut.swampium.ui.event.inventory.UICloseEvent

interface UIViewEventTarget : UIEventTarget<UIEvent> {
    @JvmDefault
    val onClose: Observable<UICloseEvent>
        get() = observable()
}
