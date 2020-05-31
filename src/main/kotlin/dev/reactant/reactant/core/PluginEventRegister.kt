package dev.reactant.reactant.core

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.service.spec.server.EventService
import org.bukkit.event.server.PluginDisableEvent

@Component
class PluginEventRegister(
        private val eventService: EventService
) : LifeCycleHook {

    override fun onEnable() {
        eventService {
            PluginDisableEvent::class.observable().map { it.plugin }.subscribe(ReactantCore.instance::onPluginDisable)
        }
    }
}
