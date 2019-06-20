package net.swamphut.swampium.ui

import net.swamphut.swampium.ui.element.ElementDisplay
import net.swamphut.swampium.ui.element.SwUIElement
import net.swamphut.swampium.ui.kits.SwUIContainerElement
import net.swamphut.swampium.ui.rendering.ElementSlot
import net.swamphut.swampium.ui.rendering.RenderedItems
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ViewInventoryContainerElement(val swUIView: SwUIView) : SwUIContainerElement("inventory") {
    override val view: UIView get() = swUIView

    override val width: Int = 9
    override val height: Int = swUIView.inventory.size
}
