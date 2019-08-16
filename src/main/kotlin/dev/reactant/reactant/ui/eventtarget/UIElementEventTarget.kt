package dev.reactant.reactant.ui.eventtarget

import dev.reactant.reactant.ui.event.UIElementEvent
import dev.reactant.reactant.ui.event.interact.element.UIElementClickEvent
import io.reactivex.Observable

interface UIElementEventTarget : UIEventTarget<UIElementEvent> {
    @JvmDefault
    val onClick: Observable<UIElementClickEvent>
        get() = observable()
}
