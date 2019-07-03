package io.reactant.reactant.core

import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.reactant.reactant.core.dependency.injection.Inject
import io.reactant.reactant.service.spec.dsl.register
import io.reactant.reactant.service.spec.server.EventService
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
