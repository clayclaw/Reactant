package dev.reactant.reactant.example

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.dsl.register
import dev.reactant.reactant.service.spec.server.EventService
import dev.reactant.reactant.ui.ReactantUIService
import org.bukkit.Bukkit
import java.util.logging.Level
import java.util.logging.Logger

@Component
class ExampleService(
        private val helloService: dev.reactant.reactant.example.HelloService,
        @Inject("plugins/ReactantExample/testers.json") private val testersConfig: Config<dev.reactant.reactant.example.TesterList>,
        private val eventService: EventService,
        private val uiService: ReactantUIService
) : LifeCycleHook {
    private val logger = Logger.getLogger(this.javaClass.name)

    override fun onEnable() {
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
            //            PlayerJoinEvent::class.observable().subscribe { showWelcome(it.player) }
        }

    }

//    fun showWelcome(player: Player) {
//        val colors = setOf(
//                createItemStack(Material.BLUE_STAINED_GLASS_PANE), createItemStack(Material.RED_STAINED_GLASS_PANE),
//                createItemStack(Material.GRAY_STAINED_GLASS_PANE), createItemStack(Material.GREEN_STAINED_GLASS_PANE)
//        )
//        uiService.createUI(player) {
//            view {
//                scheduler.interval(10).subscribe {
//                    val choose = colors.random()
//                    getElementById<ReactantUIDivElement>("main")?.fillPattern = { _, _ -> choose.clone() }
//                    render()
//                }
//            }
//            click.subscribe { it.isCancelled = true }
//            div {
//                id = "main"
//                margin(1)
//                padding(1)
//                height = MATCH_PARENT
//                fill(createItemStack(Material.RED_STAINED_GLASS_PANE))
//                div {
//                    id = "wool"
//                    height = MATCH_PARENT
//                    fill(createItemStack(Material.GREEN_WOOL))
//                    click.subscribe { showAnotherView(it.player) }
//                }
//            }
//        }
//    }
//
//    fun showAnotherView(player: Player) {
//        uiService.createUI(player, 3) {
//            div {
//                click.subscribe {
//                    it.isCancelled = true
//
//                    val headItem = ReactantUIItemElement().also { itemEl ->
//                        ReactantUIItemElementEditing(itemEl).apply {
//                            displayItem = createItemStack(Material.PLAYER_HEAD)
//                        }
//                    }
//                    view.getElementById<ReactantUIDivElement>("headContainer")?.children?.add(headItem)
//                    view.render()
//                }
//                height = MATCH_PARENT
//
//                div {
//                    width = 3
//                    height = 2
//                    display = ElementDisplay.INLINE_BLOCK
//                    // wfc what u put there
//                }
//                div {
//                    id = "headContainer"
//                    width = 3
//                    height = 2
//                    display = ElementDisplay.INLINE_BLOCK
//                    fill(createItemStack(Material.RED_STAINED_GLASS_PANE))
//                    item {
//                        displayItem = createItemStack(Material.PLAYER_HEAD)
//                    }
//                }
//            }
//        }
//    }
}
