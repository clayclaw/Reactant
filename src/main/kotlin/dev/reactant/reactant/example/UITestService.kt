package dev.reactant.reactant.example


import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs
import dev.reactant.reactant.service.spec.dsl.register
import dev.reactant.reactant.service.spec.server.EventService
import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.ReactantUIService
import org.bukkit.event.player.PlayerMoveEvent

@Component
class UITestService(
        private val eventService: EventService,
        private val uiService: ReactantUIService,
        @Inject("plugins/Reactant/testing")
        private val playerInfoConfigs: MultiConfigs<UserInfo>,
        private val schedulerService: SchedulerService
) : LifeCycleHook {

    override fun onEnable() {

        register(eventService) {
            val subscription = PlayerMoveEvent::class.observable()
                    .subscribe { }

            // die
            subscription.dispose()

        }
    }

}

data class UserInfo(
        var firstName: String,
        var lastName: String,
        var age: Int
) {
    constructor() : this("", "", 0)
}

