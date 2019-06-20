package net.swamphut.swampium.ui

import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.event.UIEvent
import net.swamphut.swampium.ui.rendering.RenderedItems
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface UIView : UIEventTrigger {
    val inventory: Inventory
    val rootElement: UIElement
    val lastRenderResult: RenderedItems?

    /**
     * Inventory view associated by player
     */
    val inventoryViews get() = inventory.viewers.mapNotNull { it as? Player }.map { it to it.openInventory }.toMap()

    @JvmDefault
    fun getElementAt(index: Int) = getElementAt(index % 9, index / 9)

    fun getElementAt(x: Int, y: Int): UIElement?

    fun render()
}