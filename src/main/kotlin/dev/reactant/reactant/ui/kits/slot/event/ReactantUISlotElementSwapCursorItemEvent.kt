package dev.reactant.uikit.element.slot

import dev.reactant.reactant.ui.event.AbstractUIElementEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ReactantUISlotElementSwapCursorItemEvent(
        override val target: ReactantUISlotElement,
        val player: Player,
        override val puttingItem: ItemStack?,
        override val takingItem: ItemStack?,
        override val from: ItemStorage?
) : AbstractUIElementEvent(target), UISlotPutItemEvent, UISlotTakeItemEvent, UISlotSwapItemEvent {
    override var isCancelled: Boolean = false
}
