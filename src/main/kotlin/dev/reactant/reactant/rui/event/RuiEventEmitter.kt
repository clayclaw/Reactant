package dev.reactant.reactant.rui.event

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.rui.RuiRootUIController
import dev.reactant.reactant.service.spec.server.EventService
import org.bukkit.event.inventory.InventoryEvent

@Component
class RuiEventEmitter(
        private val eventService: EventService,
        private val rootUIController: RuiRootUIController,
) : LifeCycleHook {
    override fun onEnable() {
        eventService {
            InventoryEvent::class.observable(true)
                    .filter { rootUIController.getRootUIByInventory(it.inventory) != null }
        }
    }
}
