package io.reactant.reactant.ui.editing

import io.reactant.reactant.ui.ReactantUIView
import io.reactant.reactant.ui.ViewInventoryContainerElement
import io.reactant.reactant.ui.event.interact.UIClickEvent
import io.reactant.reactant.ui.event.interact.UIDragEvent
import io.reactant.reactant.ui.event.inventory.UICloseEvent
import io.reactant.reactant.ui.kits.container.ReactantUIContainerElementEditing
import io.reactivex.Observable

class ReactantUIEditing(val view: ReactantUIView) : ReactantUIContainerElementEditing<ViewInventoryContainerElement>(view.rootElement) {
    fun view(action: ReactantUIView.() -> Unit) {
        view.apply(action)
    }

    val uiClose: Observable<UICloseEvent> get() = view.event.filter { it is UICloseEvent }.map { it as UICloseEvent }

    /**
     * Click event observable including both element event and ui event
     */
    val uiClick: Observable<UIClickEvent> get() = view.event.filter { it is UIClickEvent }.map { it as UIClickEvent }

    val uiDrag: Observable<UIDragEvent> get() = view.event.filter { it is UIDragEvent }.map { it as UIDragEvent }
}

