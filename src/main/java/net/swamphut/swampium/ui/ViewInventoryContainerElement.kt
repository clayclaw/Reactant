package net.swamphut.swampium.ui

import net.swamphut.swampium.ui.kits.container.SwUIContainerElement
import org.bukkit.event.inventory.InventoryType.*

class ViewInventoryContainerElement(private val swUIView: SwUIView) : SwUIContainerElement("inventory") {
    override val view: UIView get() = swUIView

    override val width: Int = when (view.inventory.type) {
        CHEST -> 9
        DISPENSER, DROPPER, CRAFTING -> 3
        else -> throw UnsupportedOperationException("Unknown inventory type: ${view.inventory.type}")
    }
    override val height: Int = swUIView.inventory.size / width
}
