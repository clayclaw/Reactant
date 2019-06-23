package net.swamphut.swampium.ui.event.interact.element

import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.event.UIElementEvent
import net.swamphut.swampium.ui.event.interact.UIClickEvent
import org.bukkit.event.inventory.InventoryClickEvent

class UIElementClickEvent(target: UIElement, override val bukkitEvent: InventoryClickEvent)
    : UIElementEvent(target), UIClickEvent {
}
