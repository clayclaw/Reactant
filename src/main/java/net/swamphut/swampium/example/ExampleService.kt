package net.swamphut.swampium.example

import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.service.spec.config.Config
import net.swamphut.swampium.service.spec.dsl.register
import net.swamphut.swampium.service.spec.server.EventService
import net.swamphut.swampium.ui.creation.createUI
import net.swamphut.swampium.ui.kits.div
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import java.util.logging.Level
import java.util.logging.Logger

@SwObject
class ExampleService(
        private val helloService: HelloService,
        @Inject("plugins/SwampiumExample/testers.json") private val testersConfig: Config<TesterList>,
        private val eventService: EventService
) : LifeCycleHook {
    private val logger = Logger.getLogger(this.javaClass.name)

    override fun init() {
        val testers = testersConfig.content.testers
        testers.map {
            """

                ==============================
                Name: ${it.name},
                Age: ${it.age},
                Address: ${it.address},
                FavouriteFoods: [${(it.favouriteFoods ?: listOf()).joinToString(",")}]
                ==============================

            """.trimIndent()
        }.forEach { logger.log(Level.INFO, it) }

        testers.forEach { it.age++ }
        testersConfig.save().subscribe()
        helloService.sayHello(Bukkit.getServer().name)


        register(eventService) {
            PlayerJoinEvent::class.observable()
                    .subscribe {
                    }
        }

    }

    val playerMenu
        get() = createUI {
            div {

            }
        }

}
