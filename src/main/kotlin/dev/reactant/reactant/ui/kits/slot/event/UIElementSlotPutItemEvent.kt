package dev.reactant.reactant.ui.kits.slot.event

import dev.reactant.reactant.ui.event.AbstractUIElementEvent
import dev.reactant.reactant.ui.event.UICancellableEvent
import dev.reactant.reactant.ui.kits.slot.ItemStorage
import dev.reactant.reactant.ui.kits.slot.ItemStorageElement
import org.bukkit.inventory.ItemStack

class UIElementSlotPutItemEvent(
        override val target: ItemStorageElement,
        override val puttingItem: ItemStack,
        override val from: ItemStorage?,
        override val isTest: Boolean
) : UICancellableEvent, AbstractUIElementEvent(target), UISlotPutItemEvent {
    override var isCancelled: Boolean = false
}
