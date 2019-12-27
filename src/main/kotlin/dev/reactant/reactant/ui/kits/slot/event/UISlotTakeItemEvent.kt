package dev.reactant.uikit.element.slot

import dev.reactant.reactant.ui.event.UICancellableEvent
import dev.reactant.reactant.ui.event.UIElementEvent
import org.bukkit.inventory.ItemStack

interface UISlotTakeItemEvent : UIElementEvent, UICancellableEvent, ItemStorageElementEvent {
    val takingItem: ItemStack?
    val from: ItemStorage?
    val isTest: Boolean
}
