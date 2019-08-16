package dev.reactant.reactant.core

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.service.spec.dsl.register
import dev.reactant.reactant.service.spec.server.EventService
import org.bukkit.event.server.PluginDisableEvent

@Component
class PluginEventRegister : LifeCycleHook {
    @Inject
    private lateinit var eventService: EventService

    override fun onEnable() {
        register(eventService) {
            PluginDisableEvent::class{ map { it.plugin }.subscribe(ReactantCore.instance::onPluginDisable) }
        }
    }
}
