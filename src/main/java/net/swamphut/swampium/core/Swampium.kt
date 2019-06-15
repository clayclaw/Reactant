package net.swamphut.swampium.core

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import net.swamphut.swampium.core.dependency.DependencyManager
import net.swamphut.swampium.core.dependency.injection.producer.SwObjectInjectableWrapper
import net.swamphut.swampium.core.swobject.BukkitPluginContainerLoader
import net.swamphut.swampium.core.swobject.container.BukkitPluginContainer
import net.swamphut.swampium.core.swobject.container.ContainerManager
import net.swamphut.swampium.core.swobject.container.SwampiumContainerManager
import net.swamphut.swampium.core.swobject.instance.SwObjectInstanceManager
import net.swamphut.swampium.core.swobject.instance.SwampiumInstanceManager
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleControlAction
import net.swamphut.swampium.core.swobject.lifecycle.SwObjectLifeCycleManager
import net.swamphut.swampium.core.swobject.lifecycle.SwObjectLifeCycleManagerImpl
import net.swamphut.swampium.extra.server.SwampiumEventService
import net.swamphut.swampium.service.spec.server.EventService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.jvm.jvmName

@SwampiumPlugin(servicePackages = ["net.swamphut.swampium"])
class Swampium : JavaPlugin() {
    val instanceManager: SwObjectInstanceManager = SwampiumInstanceManager()
    internal val swInstanceManager: SwampiumInstanceManager get() = instanceManager as SwampiumInstanceManager
    private val swObjectLifeCycleManager: SwObjectLifeCycleManager
    private val eventService: EventService
    private val containerManager: ContainerManager
    private val dependencyManager: DependencyManager

    init {
        instance = this
        swObjectLifeCycleManager = swInstanceManager.getOrConstructWithoutInjection(SwObjectLifeCycleManagerImpl::class)
        eventService = swInstanceManager.getOrConstructWithoutInjection(SwampiumEventService::class)
        containerManager = swInstanceManager.getOrConstructWithoutInjection(SwampiumContainerManager::class)
        dependencyManager = swInstanceManager.getOrConstructWithoutInjection(DependencyManager::class)
    }

    override fun onEnable() {
        @Suppress("UNUSED_VARIABLE")
        val metrics = Metrics(this)

        mainThreadScheduler = Schedulers.from { command: Runnable -> Bukkit.getServer().scheduler.runTask(this, command) }

        server.scheduler.scheduleSyncDelayedTask(this) {
            updateContainers()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun onPluginDisable(plugin: Plugin) {
        val container = containerManager.getContainer(BukkitPluginContainer.getIdentifier(plugin))
        if (container != null) {
            containerManager.getContainerProvidedInjectableWrapper(container)
                    .mapNotNull { it as? SwObjectInjectableWrapper<Any> }
                    .let {
                        swObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Save)
                        swObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Disable)
                    }
        }
    }


    private fun updateContainers() {
        Swampium.logger.info("Searching all containers")
        BukkitPluginContainerLoader.findAllLoadedPluginContainer()


        Swampium.logger.info("Resolving service dependencies")
        dependencyManager.decideRelation()

        Swampium.logger.info("Initializing services")
        @Suppress("UNCHECKED_CAST")
        swObjectLifeCycleManager.invokeAction(
                dependencyManager.dependencies
                        .mapNotNull { it as? SwObjectInjectableWrapper<Any> }
                        .onEach { if (!it.fulfilled) Swampium.logger.error("${it.swObjectClass.jvmName} missing dependencies: ${it.notFulfilledRequirements}") }
                        .filter { it.fulfilled },
                LifeCycleControlAction.Initialize)

        Swampium.logger.info("Load complete!")

    }

    @Suppress("UNCHECKED_CAST")
    override fun onDisable() {
        Swampium.logger.info("Disabling services")
        dependencyManager.dependencies.mapNotNull { it as? SwObjectInjectableWrapper<Any> }.also {
            swObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Save)
            swObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Disable)
        }
    }

    companion object {
        @JvmStatic
        lateinit var instance: Swampium
            private set

        lateinit var mainThreadScheduler: Scheduler

        var logger: Logger = LogManager.getLogger("Swampium")
        const val configDirPath = "plugins/swampium";
        const val tmpDirPath = "$configDirPath/tmp";
    }
}
