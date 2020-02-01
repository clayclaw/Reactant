package dev.reactant.reactant.ui.kits.slot.event

import dev.reactant.reactant.ui.event.AbstractUIElementEvent
import dev.reactant.reactant.ui.kits.slot.ItemStorage
import dev.reactant.reactant.ui.kits.slot.ReactantUISlotElement
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class UIElementSlotSwapHotbarItemEvent(
        override val target: ReactantUISlotElement,
        val player: Player,
        val hotbar: Int,
        override val puttingItem: ItemStack?,
        override val takingItem: ItemStack?,
        override val from: ItemStorage?
) : AbstractUIElementEvent(target), UISlotPutItemEvent, UISlotTakeItemEvent, UISlotSwapItemEvent {
    override val isTest = false
    override var isCancelled: Boolean = false
}
