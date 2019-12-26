package dev.reactant.reactant.ui.event

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.service.spec.dsl.register
import dev.reactant.reactant.service.spec.server.EventService
import dev.reactant.reactant.ui.ReactantUIService
import dev.reactant.reactant.ui.event.interact.UIClickEvent
import dev.reactant.reactant.ui.event.interact.UIDragEvent
import dev.reactant.reactant.ui.event.interact.element.UIElementClickEvent
import dev.reactant.reactant.ui.event.interact.element.UIElementDragEvent
import dev.reactant.reactant.ui.event.inventory.UICloseEvent
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
                        val clickedElement = if (isClickOnUIContainer) uiContainer.getIntractableElementAt(bukkitEvent.slot) else null

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
                                if (isDragOnUIContainer) bukkitEvent.inventorySlots.mapNotNull { uiContainer.getIntractableElementAt(it) }.distinct()
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
