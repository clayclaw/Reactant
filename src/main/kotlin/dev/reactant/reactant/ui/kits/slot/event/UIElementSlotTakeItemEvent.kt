package dev.reactant.reactant.ui.kits.slot.event

import dev.reactant.reactant.ui.event.AbstractUIElementEvent
import dev.reactant.reactant.ui.event.UICancellableEvent
import dev.reactant.reactant.ui.kits.slot.ItemStorage
import dev.reactant.reactant.ui.kits.slot.ItemStorageElement
import org.bukkit.inventory.ItemStack

class UIElementSlotTakeItemEvent(
        override val target: ItemStorageElement,
        override val takingItem: ItemStack?,
        override val from: ItemStorage?,
        override val isTest: Boolean
) : UICancellableEvent, AbstractUIElementEvent(target), UISlotTakeItemEvent {
    override var isCancelled: Boolean = false
}
