package net.swamphut.swampium.core

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

        Swampium.instance.logger.log(Level.INFO, "Initializing services")
        swObjectManager.swObjectClassMap.values
                .filter { it.state == SwObjectState.Inactive }
                .let {
                    swObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Initialize)
                }

        Swampium.instance.logger.log(Level.INFO, "Load complete!")

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
    }
}
