package io.reactant.reactant.core

import io.reactant.reactant.core.dependency.injection.Inject
import io.reactant.reactant.core.reactantobj.container.Reactant
import io.reactant.reactant.core.reactantobj.lifecycle.LifeCycleHook
import io.reactant.reactant.service.spec.dsl.register
import io.reactant.reactant.service.spec.server.EventService
import org.bukkit.event.server.PluginDisableEvent

@Reactant
class PluginEventRegister : LifeCycleHook {
    @Inject
    private lateinit var eventService: EventService

    override fun init() {
        register(eventService) {
            PluginDisableEvent::class{ map { it.plugin }.subscribe(ReactantCore.instance::onPluginDisable) }
        }
    }
}
