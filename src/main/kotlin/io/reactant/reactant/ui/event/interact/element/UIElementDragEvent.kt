package io.reactant.reactant.ui.event.interact.element

import io.reactant.reactant.ui.element.UIElement
import io.reactant.reactant.ui.event.UIElementEvent
import io.reactant.reactant.ui.event.interact.UIDragEvent
import org.bukkit.event.inventory.InventoryDragEvent

class UIElementDragEvent(target: UIElement, override val bukkitEvent: InventoryDragEvent)
    : UIElementEvent(target), UIDragEvent {

    /**
     * The slots position based on the element position
     */
    val relativeSlots: Set<Pair<Int, Int>> get() = TODO()
}
