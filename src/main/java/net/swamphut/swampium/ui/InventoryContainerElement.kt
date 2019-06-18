package net.swamphut.swampium.ui

import net.swamphut.swampium.ui.element.ElementDisplay
import net.swamphut.swampium.ui.element.SwUIElement
import net.swamphut.swampium.ui.kits.SwUIContainerElement
import net.swamphut.swampium.ui.rendering.ElementSlot
import net.swamphut.swampium.ui.rendering.RenderedItems
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class InventoryContainerElement(val inventory: Inventory) : SwUIContainerElement("inventory") {
    override val width: Int = 9
    override val height: Int = inventory.size
}
