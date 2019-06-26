package io.reactant.reactant.ui.editing

import io.reactant.reactant.ui.ReactantUIView
import io.reactant.reactant.ui.ViewInventoryContainerElement
import io.reactant.reactant.ui.kits.container.ReactantUIContainerElementEditing

class ReactantUIEditing(val view: ReactantUIView) : ReactantUIContainerElementEditing<ViewInventoryContainerElement>(view.rootElement) {
    fun view(action: ReactantUIView.() -> Unit) {
        view.apply(action)
    }
}

