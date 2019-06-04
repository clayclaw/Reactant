package net.swamphut.swampium.example

import io.reactivex.Single
import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.dependency.provide.ServiceProvider
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.extra.command.PicocliCommandService
import net.swamphut.swampium.service.spec.config.Config
import net.swamphut.swampium.service.spec.config.ConfigService
import net.swamphut.swampium.service.spec.parser.TomlParserService
import org.bukkit.Bukkit
import java.util.logging.Level
import java.util.logging.Logger

@SwObject
@ServiceProvider
class ExampleService : LifeCycleHook {
    private val logger = Logger.getLogger(this.javaClass.name)

    @Inject
    private lateinit var helloService: HelloService
//
//    @Inject
//    private lateinit var jsonParser: JsonParserService

//    @Inject
//    private lateinit var yamlParser: YamlParserService

    @Inject
    private lateinit var tomlParserService: TomlParserService
    @Inject
    private lateinit var configService: ConfigService

    @Inject
    private lateinit var commandService: PicocliCommandService

    private val testerConfigPath = "plugins/SwampiumExample/testers.toml"
    private lateinit var testersConfig: Single<Config<TesterList>>

    override fun init() {
        testersConfig = configService.loadOrDefault(tomlParserService, net.swamphut.swampium.example.TesterList::class.java, testerConfigPath, { TesterList() })
        val configContent = testersConfig.blockingGet()

        val testers = configContent.content.testers
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
        configContent.save().subscribe()
        helloService.sayHello(Bukkit.getServer().name)

        commandService.registerCommand(this) { GetTesterCommand(this) };
    }

    fun getTesters(): Single<List<TesterList.Tester>> = testersConfig.map { it.content.testers }
}
