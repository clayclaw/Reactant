package io.reactant.reactant.ui.event.interact

import io.reactant.reactant.ui.event.UICancellableEvent
import io.reactant.reactant.ui.event.UIEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryInteractEvent

/**
 * The event which is triggered by a player and cancellable
 */
interface UIInteractEvent : UIEvent, UICancellableEvent {
    val bukkitEvent: InventoryInteractEvent
    val player: Player get() = bukkitEvent.whoClicked as Player
    override var isCancelled: Boolean
        get() = bukkitEvent.isCancelled
        set(value) {
            bukkitEvent.isCancelled = value
        }
}