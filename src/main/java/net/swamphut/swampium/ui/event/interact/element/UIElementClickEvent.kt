package net.swamphut.swampium.ui.event.interact.element

import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.element.UIPropagationController
import net.swamphut.swampium.ui.event.UIElementEvent
import net.swamphut.swampium.ui.event.interact.UIClickEvent
import net.swamphut.swampium.ui.event.interact.UIInteractEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class UIElementClickEvent(target: UIElement, bukkitEvent: InventoryClickEvent)
    : UIElementEvent by UIPropagationController(target), UIClickEvent(bukkitEvent) {
}