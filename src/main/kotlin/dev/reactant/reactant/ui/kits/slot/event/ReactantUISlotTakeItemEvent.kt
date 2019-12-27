package dev.reactant.uikit.element.slot

import dev.reactant.reactant.ui.event.AbstractUIElementEvent
import dev.reactant.reactant.ui.event.UICancellableEvent
import org.bukkit.inventory.ItemStack

class ReactantUISlotTakeItemEvent(
        override val target: ItemStorageElement,
        override val takingItem: ItemStack?,
        override val from: ItemStorage?,
        override val isTest: Boolean
) : UICancellableEvent, AbstractUIElementEvent(target), UISlotTakeItemEvent {
    override var isCancelled: Boolean = false
}
