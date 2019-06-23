package net.swamphut.swampium.ui.event

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.service.spec.dsl.register
import net.swamphut.swampium.service.spec.server.EventService
import net.swamphut.swampium.ui.SwUIService
import net.swamphut.swampium.ui.event.interact.UIClickEvent
import net.swamphut.swampium.ui.event.interact.element.UIElementClickEvent
import net.swamphut.swampium.ui.event.inventory.UICloseEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*

@SwObject
class SwUIEventDistributor(
        val swUIService: SwUIService,
        val eventService: EventService
) : LifeCycleHook {
    override fun init() {
        register(eventService) {
            InventoryClickEvent::class.observable()
                    .filter { it.whoClicked is Player }
                    .filter { swUIService.inventoryUIMap.containsKey(it.whoClicked.openInventory.topInventory) }
                    .doOnError { Swampium.logger.error(it) }
                    .forEach { bukkitEvent ->
                        Swampium.logger.error("UI Clicked")
                        val uiContainer = swUIService.inventoryUIMap[bukkitEvent.whoClicked.openInventory.topInventory]!!

                        val isClickOnUIContainer = bukkitEvent.clickedInventory == uiContainer.inventory
                        val clickedElement = if (isClickOnUIContainer) uiContainer.getElementAt(bukkitEvent.slot) else null
                        when {
                            isClickOnUIContainer && clickedElement != null -> UIElementClickEvent(clickedElement, bukkitEvent).propagateTo(clickedElement)
                            else -> object : UIClickEvent {
                                override val bukkitEvent = bukkitEvent
                            }.propagateTo(uiContainer)
                        }
                    }

            InventoryCloseEvent::class.observable()
                    .filter { swUIService.inventoryUIMap.containsKey(it.view.topInventory) }  // it is ui view
                    .subscribe { swUIService.inventoryUIMap[it.view.topInventory]!!.event.onNext(UICloseEvent(it)) }


            InventoryDragEvent::class.observable()
            InventoryMoveItemEvent::class.observable()
            InventoryPickupItemEvent::class.observable()
        }
    }
}
