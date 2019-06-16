package net.swamphut.swampium.ui.element

import net.swamphut.swampium.ui.element.collection.SwUIElementChildrenSet
import net.swamphut.swampium.ui.element.collection.SwUIElementClassSet


abstract class SwUIElement : UIElement {
    private var _parent: UIElement? = null
    override var parent: UIElement?
        get() = _parent
        set(newParent) {
            if (newParent == _parent) return
            val originParent = _parent
            _parent = newParent

            originParent?.children?.remove(this)
            newParent?.children?.add(this)
        }

    @Suppress("LeakingThis")
    override val children = SwUIElementChildrenSet(this)

    final override val attributes = HashMap<String, String?>()

    override var id: String? by attributes

    override var classList = SwUIElementClassSet(attributes)
}
