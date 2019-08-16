package dev.reactant.reactant.ui.element.collection

import dev.reactant.reactant.ui.element.UIElement

class ReactantUIElementChildrenSet private constructor(
        private val parent: UIElement,
        private val childrenList: LinkedHashSet<UIElement>
) : MutableSet<UIElement> by childrenList {
    constructor(parent: UIElement) : this(parent, LinkedHashSet())

    override fun add(element: UIElement): Boolean = childrenList.add(element).also {
        if (element.parent != parent) element.parent = parent
    }

    override fun remove(element: UIElement): Boolean = childrenList.remove(element).also {
        if (element.parent == parent) element.parent = null
    }

}
