package net.swamphut.swampium.ui.event

import org.bukkit.entity.Player

interface UIInteractEvent : UIEvent {
    val player: Player
}