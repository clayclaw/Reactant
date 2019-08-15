package io.reactant.reactant.ui.event

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.reactant.reactant.service.spec.dsl.register
import io.reactant.reactant.service.spec.server.EventService
import io.reactant.reactant.ui.ReactantUIService
import io.reactant.reactant.ui.event.interact.UIClickEvent
import io.reactant.reactant.ui.event.interact.UIDragEvent
import io.reactant.reactant.ui.event.interact.element.UIElementClickEvent
import io.reactant.reactant.ui.event.interact.element.UIElementDragEvent
import io.reactant.reactant.ui.event.inventory.UICloseEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

@Component
class ReactantUIEventDistributor(
        val reactantUIService: ReactantUIService,
        val eventService: EventService
) : LifeCycleHook {
    override fun onEnable() {
        register(eventService) {
            InventoryClickEvent::class.observable()
                    .filter { it.whoClicked is Player }
                    .filter { reactantUIService.inventoryUIMap.containsKey(it.view.topInventory) }
                    .doOnError { ReactantCore.logger.error(it) }
                    .forEach { bukkitEvent ->
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
                    .doOnError { ReactantCore.logger.error(it) }
                    .subscribe { reactantUIService.inventoryUIMap[it.view.topInventory]!!.event.onNext(UICloseEvent(it)) }


            InventoryDragEvent::class.observable()
                    .filter { it.whoClicked is Player }
                    .filter { reactantUIService.inventoryUIMap.containsKey(it.view.topInventory) }
                    .doOnError { ReactantCore.logger.error(it) }
                    .forEach { bukkitEvent ->
                        val uiContainer = reactantUIService.inventoryUIMap[bukkitEvent.whoClicked.openInventory.topInventory]!!

                        val inventories = bukkitEvent.rawSlots.map { bukkitEvent.view.getInventory(it) }.distinct()
                        val isDragOnUIContainer = inventories.size == 1 && inventories[0] == uiContainer.inventory
                        val draggingElements =
                                if (isDragOnUIContainer) bukkitEvent.inventorySlots.mapNotNull { uiContainer.getElementAt(it) }.distinct()
                                else listOf()

                        when {
                            isDragOnUIContainer && draggingElements.size == 1 ->
                                UIElementDragEvent(draggingElements[0], bukkitEvent).propagateTo(draggingElements[0])
                            else -> object : UIDragEvent {
                                override val bukkitEvent = bukkitEvent
                            }.propagateTo(uiContainer)
                        }
                    }
        }
    }
}
