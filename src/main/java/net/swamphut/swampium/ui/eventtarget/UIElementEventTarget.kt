package net.swamphut.swampium.ui.eventtarget

import io.reactivex.Observable
import net.swamphut.swampium.ui.event.UIElementEvent
import net.swamphut.swampium.ui.event.interact.element.UIElementClickEvent

interface UIElementEventTarget : UIEventTarget<UIElementEvent> {
    @JvmDefault
    val onClick: Observable<UIElementClickEvent>
        get() = observable()
}
