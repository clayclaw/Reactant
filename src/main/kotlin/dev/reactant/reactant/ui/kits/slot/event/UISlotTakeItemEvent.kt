package dev.reactant.reactant.ui.kits.slot.event

import dev.reactant.reactant.ui.event.UICancellableEvent
import dev.reactant.reactant.ui.event.UIElementEvent
import dev.reactant.reactant.ui.kits.slot.ItemStorage
import org.bukkit.inventory.ItemStack

interface UISlotTakeItemEvent : UIElementEvent, UICancellableEvent, ItemStorageElementEvent {
    val takingItem: ItemStack?
    val from: ItemStorage?
    val isTest: Boolean
}
