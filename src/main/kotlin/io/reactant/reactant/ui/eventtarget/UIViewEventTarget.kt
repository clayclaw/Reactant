package io.reactant.reactant.ui.eventtarget

import io.reactant.reactant.ui.event.UIEvent
import io.reactant.reactant.ui.event.inventory.UICloseEvent
import io.reactivex.Observable

interface UIViewEventTarget : UIEventTarget<UIEvent> {
    @JvmDefault
    val onClose: Observable<UICloseEvent>
        get() = observable()
}
