package io.reactant.reactant.ui.event.interact

import org.bukkit.event.inventory.InventoryDragEvent

interface UIDragEvent : UIInteractEvent {
    override val bukkitEvent: InventoryDragEvent
}
