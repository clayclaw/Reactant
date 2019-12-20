package dev.reactant.reactant.ui.editing

import dev.reactant.reactant.ui.ReactantUIView
import dev.reactant.reactant.ui.ViewInventoryContainerElement
import dev.reactant.reactant.ui.event.interact.UIClickEvent
import dev.reactant.reactant.ui.event.interact.UIDragEvent
import dev.reactant.reactant.ui.event.inventory.UICloseEvent
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElementEditing
import io.reactivex.Observable

class ReactantUIEditing(val view: ReactantUIView) : ReactantUIContainerElementEditing<ViewInventoryContainerElement>(view.rootElement) {
    val uiClose: Observable<UICloseEvent> get() = view.event.filter { it is UICloseEvent }.map { it as UICloseEvent }

    val scheduler get() = view.scheduler

    /**
     * Click event observable including both element event and ui event
     */
    val uiClick: Observable<UIClickEvent> get() = view.event.filter { it is UIClickEvent }.map { it as UIClickEvent }

    val uiDrag: Observable<UIDragEvent> get() = view.event.filter { it is UIDragEvent }.map { it as UIDragEvent }
}

