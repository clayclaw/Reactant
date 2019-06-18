package net.swamphut.swampium.ui.event

import net.swamphut.swampium.ui.element.UIElement
import org.bukkit.entity.Player

class SwUIClickEvent(target: UIElement, override val player: Player) : SwUIEvent(target), UIInteractEvent {

}