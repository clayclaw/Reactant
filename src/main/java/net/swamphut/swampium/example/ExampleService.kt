package net.swamphut.swampium.example

import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.service.spec.config.Config
import net.swamphut.swampium.service.spec.dsl.register
import net.swamphut.swampium.service.spec.server.EventService
import net.swamphut.swampium.ui.SwUIService
import net.swamphut.swampium.ui.element.UIElement.Companion.MATCH_PARENT
import net.swamphut.swampium.ui.kits.SwUIDivElement
import net.swamphut.swampium.ui.kits.div
import net.swamphut.swampium.ui.query.getElementById
import net.swamphut.swampium.utils.content.item.createItemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import java.util.logging.Level
import java.util.logging.Logger

@SwObject
class ExampleService(
        private val helloService: HelloService,
        @Inject("plugins/SwampiumExample/testers.json") private val testersConfig: Config<TesterList>,
        private val eventService: EventService,
        private val uiService: SwUIService
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

        helloService.sayHello(Bukkit.getServer().name)

        register(eventService) {
            PlayerJoinEvent::class.observable().subscribe { showWelcome(it.player) }
        }

    }

    fun showWelcome(player: Player) {
        val colors = setOf(
                createItemStack(Material.BLUE_STAINED_GLASS_PANE), createItemStack(Material.RED_STAINED_GLASS_PANE),
                createItemStack(Material.GRAY_STAINED_GLASS_PANE), createItemStack(Material.GREEN_STAINED_GLASS_PANE)
        )
        uiService.createUI(player) {
            view {
                scheduler.interval(10).subscribe {
                    val choose = colors.random()
                    getElementById<SwUIDivElement>("main")?.fillPattern = { _, _ -> choose.clone() }
                    render()
                }
            }
            click.subscribe { it.isCancelled = true }
            div {
                id = "main"
                margin(1)
                padding(1)
                height = MATCH_PARENT
                fill(createItemStack(Material.RED_STAINED_GLASS_PANE))
                div {
                    id = "wool"
                    height = MATCH_PARENT
                    fill(createItemStack(Material.GREEN_WOOL))
                }
            }
        }
    }

    fun showAnotherView(player: Player) {
        uiService.createUI(player) {
            div {
                click.subscribe { it.isCancelled = true }
                padding = listOf(1)
                height = MATCH_PARENT
                fill(createItemStack(Material.RED_STAINED_GLASS_PANE))
                div {
                    height = MATCH_PARENT
                    fill(createItemStack(Material.GREEN_WOOL))
                }
            }
        }
    }
}
