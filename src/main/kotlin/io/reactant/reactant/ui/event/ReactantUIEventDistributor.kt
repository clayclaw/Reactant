package io.reactant.reactant.ui.event

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.reactant.reactant.service.spec.dsl.register
import io.reactant.reactant.service.spec.server.EventService
import io.reactant.reactant.ui.ReactantUIService
import io.reactant.reactant.ui.event.interact.UIClickEvent
import io.reactant.reactant.ui.event.interact.element.UIElementClickEvent
import io.reactant.reactant.ui.event.inventory.UICloseEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*

@Component
class ReactantUIEventDistributor(
        val reactantUIService: ReactantUIService,
        val eventService: EventService
) : LifeCycleHook {
    override fun onEnable() {
        register(eventService) {
            InventoryClickEvent::class.observable()
                    .filter { it.whoClicked is Player }
                    .filter { reactantUIService.inventoryUIMap.containsKey(it.whoClicked.openInventory.topInventory) }
                    .doOnError { ReactantCore.logger.error(it) }
                    .forEach { bukkitEvent ->
                        ReactantCore.logger.error("UI Clicked")
                        val uiContainer = reactantUIService.inventoryUIMap[bukkitEvent.whoClicked.openInventory.topInventory]!!

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
                    .filter { reactantUIService.inventoryUIMap.containsKey(it.view.topInventory) }  // it is ui view
                    .subscribe { reactantUIService.inventoryUIMap[it.view.topInventory]!!.event.onNext(UICloseEvent(it)) }


            InventoryDragEvent::class.observable()
            InventoryMoveItemEvent::class.observable()
            InventoryPickupItemEvent::class.observable()
        }
    }
}
