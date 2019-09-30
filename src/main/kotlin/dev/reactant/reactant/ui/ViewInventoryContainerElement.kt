package dev.reactant.reactant.ui

import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElement
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElementEditing
import org.bukkit.event.inventory.InventoryType.*

class ViewInventoryContainerElement(private val reactantUIView: ReactantUIView) : ReactantUIContainerElement("inventory") {
    override fun edit() = object : ReactantUIContainerElementEditing<ViewInventoryContainerElement>(this) {}

    override val view: UIView get() = reactantUIView

    override var parent: UIElement?
        get() = null
        set(value) = throw UnsupportedOperationException("View element cannot have parent")

    override val width: Int = when (view.inventory.type) {
        CHEST -> 9
        DISPENSER, DROPPER, CRAFTING -> 3
        else -> throw UnsupportedOperationException("Unknown inventory type: ${view.inventory.type}")
    }
    override val height: Int = reactantUIView.inventory.size / width
}

