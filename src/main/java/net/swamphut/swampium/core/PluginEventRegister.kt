package net.swamphut.swampium.core

import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.service.spec.dsl.register
import net.swamphut.swampium.service.spec.server.EventService
import org.bukkit.event.server.PluginDisableEvent

@SwObject
class PluginEventRegister : LifeCycleHook {
    @Inject
    private lateinit var eventService: EventService

    override fun init() {
        register(eventService) {
            PluginDisableEvent::class{ map { it.plugin }.subscribe(Swampium.instance::onPluginDisable) }
        }
    }
}
