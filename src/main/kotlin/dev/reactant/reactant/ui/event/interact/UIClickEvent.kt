package dev.reactant.reactant.ui.event.interact

import org.bukkit.event.inventory.InventoryClickEvent

interface UIClickEvent : UIInteractEvent {
    override val bukkitEvent: InventoryClickEvent
}
