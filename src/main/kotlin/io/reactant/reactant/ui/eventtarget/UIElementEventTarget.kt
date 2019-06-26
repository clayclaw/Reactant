package io.reactant.reactant.ui.eventtarget

import io.reactant.reactant.ui.event.UIElementEvent
import io.reactant.reactant.ui.event.interact.element.UIElementClickEvent
import io.reactivex.Observable

interface UIElementEventTarget : UIEventTarget<UIElementEvent> {
    @JvmDefault
    val onClick: Observable<UIElementClickEvent>
        get() = observable()
}
