package dev.reactant.reactant.ui

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.event.UIEvent
import dev.reactant.reactant.ui.eventtarget.UIEventTarget
import dev.reactant.reactant.ui.query.UIQueryable
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface UIView : UIEventTarget<UIEvent>, UIQueryable {
    val inventory: Inventory
    val rootElement: UIElement

    /**
     * The scheduler which will automatically dispose all observable when view be destroyed
     */
    val scheduler: SchedulerService

    /**
     * Inventory view associated by player
     */
    val inventoryViews get() = inventory.viewers.mapNotNull { it as? Player }.map { it to it.openInventory }.toMap()

    @JvmDefault
    fun getElementAt(index: Int) = getElementAt(index % 9, index / 9)

    fun getElementAt(x: Int, y: Int): UIElement?

    /**
     * Render the inventory elements and Update inventory view in next tick
     */
    fun render()

    fun show(player: Player)
}
