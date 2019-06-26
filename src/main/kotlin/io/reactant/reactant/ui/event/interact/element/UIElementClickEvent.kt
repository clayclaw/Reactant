package io.reactant.reactant.ui.event.interact.element

import io.reactant.reactant.ui.element.UIElement
import io.reactant.reactant.ui.event.UIElementEvent
import io.reactant.reactant.ui.event.interact.UIClickEvent
import org.bukkit.event.inventory.InventoryClickEvent

class UIElementClickEvent(target: UIElement, override val bukkitEvent: InventoryClickEvent)
    : UIElementEvent(target), UIClickEvent {
}
