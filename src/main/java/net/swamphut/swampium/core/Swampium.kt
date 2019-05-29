package net.swamphut.swampium.core

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import net.swamphut.swampium.core.swobject.BukkitPluginContainerLoader
import net.swamphut.swampium.core.swobject.SwObjectManager
import net.swamphut.swampium.core.swobject.SwObjectState
import net.swamphut.swampium.core.swobject.instance.InstanceManager
import net.swamphut.swampium.core.swobject.instance.SwampiumInstanceManager
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleControlAction
import net.swamphut.swampium.core.swobject.lifecycle.SwObjectLifeCycleManager
import net.swamphut.swampium.core.swobject.lifecycle.SwObjectLifeCycleManagerImpl
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

@SwampiumPlugin(servicePackages = ["net.swamphut.swampium"])
class Swampium : JavaPlugin() {
    val instanceManager: InstanceManager
    private val swObjectManager: SwObjectManager
    private val swObjectLifeCycleManager: SwObjectLifeCycleManager

    init {
        instance = this
        instanceManager = SwampiumInstanceManager()
        swObjectManager = instanceManager.getInstance(SwObjectManager::class.java)
        swObjectLifeCycleManager = instanceManager.getInstance(SwObjectLifeCycleManagerImpl::class.java)
    }

    override fun onEnable() {
        @Suppress("UNUSED_VARIABLE")
        val metrics = Metrics(this)

        mainThreadScheduler = Schedulers.from { command: Runnable -> Bukkit.getServer().scheduler.runTask(this, command) }

        server.scheduler.scheduleSyncDelayedTask(this) {
            onPluginsLoaded()
        }
    }

    fun onPluginsLoaded() {
        Swampium.logger.info("Searching all containers")
        BukkitPluginContainerLoader.findAllLoadedPluginContainer()


        Swampium.logger.info("Resolving service dependencies")
        swObjectManager.injectAllSwObject()
        showNotFulfilledSwObject()

        Swampium.logger.info("Initializing services")
        swObjectManager.swObjectClassMap.values
                .filter { it.state == SwObjectState.Inactive }
                .let {
                    val success = swObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Initialize)
                    if (!success) {
                        val failed = it.filter { it.state != SwObjectState.Active }
                        Swampium.logger.info("${failed.size} SwObject failed to initialize!")
                        failed.forEach { swObject ->
                            swObject.lifeCycleActionExceptions.forEach { exception ->
                                Swampium.logger.error(swObject.javaClass.canonicalName, exception)
                            }
                        }
                    }
                }

        Swampium.logger.info("Load complete!")

    }

    fun showNotFulfilledSwObject() {
        val notFulfilledSwObjects = swObjectManager.swObjectClassMap.values
                .filter { !it.fulfilled };
        if (notFulfilledSwObjects.isNotEmpty()) {
            var message = "Some SwObjects' dependencies are not fulfilled:\n";
            notFulfilledSwObjects.forEach { notFulfilled ->
                val missingServices = notFulfilled.requiredServices
                        .filter { !notFulfilled.requiredServicesResolvedResult.containsKey(it) }
                        .map { "\t\t- ${it.canonicalName}" }
                        .joinToString("\n")
                message += "\t${notFulfilled.instance::class.java.canonicalName}:\n"
                message += "$missingServices\n"

            }
            message += "To solve the problem, ensure you have correctly install all required dependencies.";
            Swampium.logger.error(message);
        }
    }

    override fun onDisable() {
        Swampium.logger.info("Disabling services")
        swObjectManager.swObjectClassMap.values
                .filter { it.state == SwObjectState.Active }
                .let {
                    swObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Save)
                    swObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Disable)
                }
    }

    companion object {
        @JvmStatic
        lateinit var instance: Swampium

        lateinit var mainThreadScheduler: Scheduler

        val logger: Logger = LogManager.getLogger("Swampium")
    }
}
