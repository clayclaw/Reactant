package net.swamphut.swampium.ui.event.interact

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryInteractEvent

open class UIClickEvent(override val bukkitEvent: InventoryClickEvent) : UIInteractEvent {
}