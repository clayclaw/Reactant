package io.reactant.reactant.ui

import io.reactant.reactant.ui.element.UIElement
import io.reactant.reactant.ui.kits.container.ReactantUIContainerElement
import org.bukkit.event.inventory.InventoryType.*

class ViewInventoryContainerElement(private val reactantUIView: ReactantUIView) : ReactantUIContainerElement("inventory") {
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
