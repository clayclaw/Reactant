package dev.reactant.rui.events

import dev.reactant.rui.render.ElementDOMTreeNode
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.inventory.InventoryInteractEvent

enum class RuiEventPhase {
    Capturing,
    AtTarget,
    Bubbling
}

abstract class RuiEvent<T : Event>(
        val bukkitEvent: T,
        val target: ElementDOMTreeNode
) {
    var eventPhase: RuiEventPhase = RuiEventPhase.Capturing
        internal set

    private var propagation = true

    fun stopPropagation() {
        propagation = false
    }
}

abstract class RuiInventoryEvent<T : InventoryEvent>(
        bukkitEvent: T, target: ElementDOMTreeNode
) : RuiEvent<T>(bukkitEvent, target)


abstract class RuiInventoryInteractEvent<T : InventoryInteractEvent>(
        bukkitEvent: T, target: ElementDOMTreeNode
) : RuiInventoryEvent<T>(bukkitEvent, target)


open class RuiClickEvent(
        bukkitEvent: InventoryClickEvent, target: ElementDOMTreeNode
) : RuiInventoryInteractEvent<InventoryClickEvent>(bukkitEvent, target)

open class RuiDragEvent(
        bukkitEvent: InventoryDragEvent, target: ElementDOMTreeNode
) : RuiInventoryInteractEvent<InventoryDragEvent>(bukkitEvent, target)
