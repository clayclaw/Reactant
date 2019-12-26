package dev.reactant.reactant.ui.event.interact.element

import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.event.AbstractUIElementEvent
import dev.reactant.reactant.ui.event.interact.UIDragEvent
import org.bukkit.event.inventory.InventoryDragEvent

class UIElementDragEvent(target: UIElement, override val bukkitEvent: InventoryDragEvent)
    : AbstractUIElementEvent(target), UIDragEvent {

    /**
     * The slots position based on the element position
     */
    val relativeSlots: Set<Pair<Int, Int>> get() = TODO()
}
