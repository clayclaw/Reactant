package net.swamphut.swampium.example

import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.dependency.ServiceProvider
import net.swamphut.swampium.core.swobject.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.service.spec.config.ConfigService
import net.swamphut.swampium.service.spec.parser.JsonParserService
import org.bukkit.Bukkit
import java.util.logging.Level
import java.util.logging.Logger

@SwObject
@ServiceProvider
class ExampleService : LifeCycleHook {
    private val logger = Logger.getLogger(this.javaClass.name)

    @Inject
    private lateinit var helloService: HelloService

    @Inject
    private lateinit var jsonParser: JsonParserService

    @Inject
    private lateinit var configService: ConfigService

    override fun init() {
        val testerConfigPath = "plugins/SwampiumExample/testers.json"
        val testersConfig = configService.loadOrDefault(jsonParser, TesterList::class.java, testerConfigPath, { TesterList() })
                .blockingGet()

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
    }
}
