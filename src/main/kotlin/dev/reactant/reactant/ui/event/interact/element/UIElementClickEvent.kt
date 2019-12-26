package dev.reactant.reactant.ui.event.interact.element

import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.event.AbstractUIElementEvent
import dev.reactant.reactant.ui.event.interact.UIClickEvent
import org.bukkit.event.inventory.InventoryClickEvent

class UIElementClickEvent(target: UIElement, override val bukkitEvent: InventoryClickEvent)
    : AbstractUIElementEvent(target), UIClickEvent {
}
