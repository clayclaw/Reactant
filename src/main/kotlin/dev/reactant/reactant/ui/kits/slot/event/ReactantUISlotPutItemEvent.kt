package dev.reactant.uikit.element.slot

import dev.reactant.reactant.ui.event.AbstractUIElementEvent
import dev.reactant.reactant.ui.event.UICancellableEvent
import org.bukkit.inventory.ItemStack

class ReactantUISlotPutItemEvent(
        override val target: ItemStorageElement,
        override val puttingItem: ItemStack,
        override val from: ItemStorage?,
        override val isTest: Boolean
) : UICancellableEvent, AbstractUIElementEvent(target), UISlotPutItemEvent {
    override var isCancelled: Boolean = false
}
