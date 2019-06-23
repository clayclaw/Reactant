package net.swamphut.swampium.ui.event.inventory

import net.swamphut.swampium.ui.event.UIEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent

class UICloseEvent(val bukkitEvent: InventoryCloseEvent) : UIEvent {
    val player: Player = bukkitEvent.player as Player
}
