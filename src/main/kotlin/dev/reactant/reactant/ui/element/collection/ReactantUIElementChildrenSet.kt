package dev.reactant.reactant.ui.element.collection

import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElement

class ReactantUIElementChildrenSet private constructor(
        private val parent: ReactantUIElement?,
        private val childrenList: LinkedHashSet<UIElement>
) : MutableSet<UIElement> by childrenList {
    constructor(parent: ReactantUIElement?) : this(parent, LinkedHashSet())

    override fun add(element: UIElement): Boolean {
        if (element !is ReactantUIElement) throw UnsupportedOperationException("ReactantUIElement can only add ReactantUIElement as child")
        return childrenList.add(element).also {
            if (element.parent != parent) element.parent = parent
            element.view?.render()
        }
    }

    override fun remove(element: UIElement): Boolean = childrenList.remove(element).also {
        element.view?.render()
        if (element.parent == parent) element.parent = null
    }

}
