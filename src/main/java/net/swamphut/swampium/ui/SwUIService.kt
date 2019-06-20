package net.swamphut.swampium.ui

import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.service.spec.dsl.register
import net.swamphut.swampium.service.spec.server.EventService
import net.swamphut.swampium.ui.event.interact.UIClickEvent
import net.swamphut.swampium.ui.event.interact.element.UIElementClickEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory

@SwObject
class SwUIService(
        val event: EventService
) : LifeCycleHook {
    val inventoryUIMap = HashMap<Inventory, UIView>()
    override fun init() {
        register(event) {
            InventoryClickEvent::class.observable()
                    .filter { it.whoClicked is Player }
                    .filter { inventoryUIMap.containsKey(it.whoClicked.openInventory.topInventory) }
                    .forEach { bukkitEvent ->
                        val uiContainer = inventoryUIMap[bukkitEvent.whoClicked.openInventory.topInventory]!!

                        val isClickOnUIContainer = bukkitEvent.clickedInventory == uiContainer.inventory
                        val clickedElement = if (isClickOnUIContainer) uiContainer.getElementAt(bukkitEvent.slot) else null
                        when {
                            isClickOnUIContainer && clickedElement != null -> UIElementClickEvent(clickedElement, bukkitEvent).propagateTo(clickedElement)
                            else -> UIClickEvent(bukkitEvent).propagateTo(uiContainer)
                        }
                    }

            InventoryDragEvent::class.observable()
            InventoryMoveItemEvent::class.observable()
            InventoryPickupItemEvent::class.observable()

            InventoryCloseEvent::class.observable()
        }
    }

    fun showUI(player: Player, uiView: UIView) {
        val view = uiView.inventory
        player.openInventory(view)
    }
}

