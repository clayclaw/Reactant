package net.swamphut.swampium.core

import io.reactivex.schedulers.Schedulers
import net.swamphut.swampium.core.swobject.BukkitPluginContainerLoader
import net.swamphut.swampium.core.swobject.SwObjectManager
import net.swamphut.swampium.core.swobject.SwObjectState
import net.swamphut.swampium.core.swobject.instance.InstanceManager
import net.swamphut.swampium.core.swobject.instance.SwampiumInstanceManager
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleControlAction
import net.swamphut.swampium.core.swobject.lifecycle.SwObjectLifeCycleManager
import net.swamphut.swampium.core.swobject.lifecycle.SwObjectLifeCycleManagerImpl
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

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

        server.scheduler.scheduleSyncDelayedTask(this) {
            onPluginsLoaded()
        }
    }

    fun onPluginsLoaded() {
        Swampium.instance.logger.log(Level.INFO, "Searching all containers")
        BukkitPluginContainerLoader.findAllLoadedPluginContainer()


        Swampium.instance.logger.log(Level.INFO, "Resolving service dependencies")
        swObjectManager.injectAllSwObject()
        showNotFulfilledSwObject()

        Swampium.instance.logger.log(Level.INFO, "Initializing services")
        swObjectManager.swObjectClassMap.values
                .filter { it.state == SwObjectState.Inactive }
                .let {
                    swObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Initialize)
                }

        Swampium.instance.logger.log(Level.INFO, "Load complete!")

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
            Swampium.instance.logger.log(Level.SEVERE, message);
        }
    }

    override fun onDisable() {
        Swampium.instance.logger.log(Level.INFO, "Disabling services")
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

        val mainThreadScheduler = Schedulers.from(Runnable::run);
    }
}
