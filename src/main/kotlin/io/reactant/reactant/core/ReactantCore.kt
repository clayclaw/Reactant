package io.reactant.reactant.core

import io.reactant.reactant.core.component.BukkitPluginContainerLoader.findAllLoadedPluginContainer
import io.reactant.reactant.core.component.container.BukkitPluginContainer
import io.reactant.reactant.core.component.container.ContainerManager
import io.reactant.reactant.core.component.container.ReactantContainerManager
import io.reactant.reactant.core.component.instance.ComponentInstanceManager
import io.reactant.reactant.core.component.instance.ReactantInstanceManager
import io.reactant.reactant.core.component.lifecycle.ComponentLifeCycleManager
import io.reactant.reactant.core.component.lifecycle.ComponentLifeCycleManagerImpl
import io.reactant.reactant.core.component.lifecycle.LifeCycleControlAction
import io.reactant.reactant.core.dependency.ProviderManager
import io.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

@ReactantPlugin(servicePackages = ["io.reactant.reactant"])
class ReactantCore : JavaPlugin() {
    val instanceManager: ComponentInstanceManager = ReactantInstanceManager()
    internal val reactantInstanceManager: ReactantInstanceManager get() = instanceManager as ReactantInstanceManager
    private val componentLifeCycleManager: ComponentLifeCycleManager
    private val containerManager: ContainerManager
    private val providerManager: ProviderManager

    init {
        instance = this
        componentLifeCycleManager = reactantInstanceManager.getOrConstructWithoutInjection(ComponentLifeCycleManagerImpl::class)
        containerManager = reactantInstanceManager.getOrConstructWithoutInjection(ReactantContainerManager::class)
        providerManager = reactantInstanceManager.getOrConstructWithoutInjection(ProviderManager::class)
    }

    override fun onEnable() {
        @Suppress("UNUSED_VARIABLE")
        val metrics = Metrics(this)

        ReactantCore.mainThreadScheduler = Schedulers.from { command: Runnable -> Bukkit.getServer().scheduler.runTask(this, command) }

        server.scheduler.scheduleSyncDelayedTask(this) {
            updateContainers()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun onPluginDisable(plugin: Plugin) {
        val container = containerManager.getContainer(BukkitPluginContainer.getIdentifier(plugin))
        if (container != null) {
            containerManager.getContainerProvidedInjectableWrapper(container)
                    .mapNotNull { it as? ComponentProvider<Any> }
                    .let {
                        componentLifeCycleManager.invokeAction(it, LifeCycleControlAction.Save)
                        componentLifeCycleManager.invokeAction(it, LifeCycleControlAction.Disable)
                    }
        }
    }


    private fun updateContainers() {
        ReactantCore.logger.info("Searching all containers")
        findAllLoadedPluginContainer()


        ReactantCore.logger.info("Resolving service providers")
        providerManager.decideRelation()

        ReactantCore.logger.info("Initializing services")
        @Suppress("UNCHECKED_CAST")
        componentLifeCycleManager.invokeAction(
                providerManager.providers
                        .mapNotNull { it as? ComponentProvider<Any> }
                        .onEach { if (!it.fulfilled) ReactantCore.logger.error("${it.componentClass.jvmName} missing providers: ${it.notFulfilledRequirements}") }
                        .filter { it.fulfilled },
                LifeCycleControlAction.Initialize)

        ReactantCore.logger.info("Load complete!")

    }

    @Suppress("UNCHECKED_CAST")
    override fun onDisable() {
        ReactantCore.logger.info("Disabling services")
        providerManager.providers.mapNotNull { it as? ComponentProvider<Any> }.also {
            componentLifeCycleManager.invokeAction(it, LifeCycleControlAction.Save)
            componentLifeCycleManager.invokeAction(it, LifeCycleControlAction.Disable)
        }
    }

    companion object {
        @JvmStatic
        lateinit var instance: ReactantCore
            private set

        lateinit var mainThreadScheduler: Scheduler

        val logger: Logger = LogManager.getLogger("ReactantCore")
        const val configDirPath = "plugins/Reactant";
        const val tmpDirPath = "${ReactantCore.configDirPath}/tmp";
    }
}
