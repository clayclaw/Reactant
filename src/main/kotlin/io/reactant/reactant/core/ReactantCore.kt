package io.reactant.reactant.core

import io.reactant.reactant.core.dependency.DependencyManager
import io.reactant.reactant.core.dependency.injection.producer.ReactantObjectInjectableWrapper
import io.reactant.reactant.core.reactantobj.BukkitPluginContainerLoader.findAllLoadedPluginContainer
import io.reactant.reactant.core.reactantobj.container.BukkitPluginContainer
import io.reactant.reactant.core.reactantobj.container.ContainerManager
import io.reactant.reactant.core.reactantobj.container.ReactantContainerManager
import io.reactant.reactant.core.reactantobj.instance.ReactantInstanceManager
import io.reactant.reactant.core.reactantobj.instance.ReactantObjectInstanceManager
import io.reactant.reactant.core.reactantobj.lifecycle.LifeCycleControlAction
import io.reactant.reactant.core.reactantobj.lifecycle.ReactantObjectLifeCycleManager
import io.reactant.reactant.core.reactantobj.lifecycle.ReactantObjectLifeCycleManagerImpl
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.jvm.jvmName

@ReactantPlugin(servicePackages = ["io.reactant.reactant"])
class ReactantCore : JavaPlugin() {
    val instanceManager: ReactantObjectInstanceManager = ReactantInstanceManager()
    internal val reactantInstanceManager: ReactantInstanceManager get() = instanceManager as ReactantInstanceManager
    private val reactantObjectLifeCycleManager: ReactantObjectLifeCycleManager
    private val containerManager: ContainerManager
    private val dependencyManager: DependencyManager

    init {
        instance = this
        reactantObjectLifeCycleManager = reactantInstanceManager.getOrConstructWithoutInjection(ReactantObjectLifeCycleManagerImpl::class)
        containerManager = reactantInstanceManager.getOrConstructWithoutInjection(ReactantContainerManager::class)
        dependencyManager = reactantInstanceManager.getOrConstructWithoutInjection(DependencyManager::class)
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
                    .mapNotNull { it as? ReactantObjectInjectableWrapper<Any> }
                    .let {
                        reactantObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Save)
                        reactantObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Disable)
                    }
        }
    }


    private fun updateContainers() {
        ReactantCore.logger.info("Searching all containers")
        findAllLoadedPluginContainer()


        ReactantCore.logger.info("Resolving service dependencies")
        dependencyManager.decideRelation()

        ReactantCore.logger.info("Initializing services")
        @Suppress("UNCHECKED_CAST")
        reactantObjectLifeCycleManager.invokeAction(
                dependencyManager.dependencies
                        .mapNotNull { it as? ReactantObjectInjectableWrapper<Any> }
                        .onEach { if (!it.fulfilled) ReactantCore.logger.error("${it.reactantObjectClass.jvmName} missing dependencies: ${it.notFulfilledRequirements}") }
                        .filter { it.fulfilled },
                LifeCycleControlAction.Initialize)

        ReactantCore.logger.info("Load complete!")

    }

    @Suppress("UNCHECKED_CAST")
    override fun onDisable() {
        ReactantCore.logger.info("Disabling services")
        dependencyManager.dependencies.mapNotNull { it as? ReactantObjectInjectableWrapper<Any> }.also {
            reactantObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Save)
            reactantObjectLifeCycleManager.invokeAction(it, LifeCycleControlAction.Disable)
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
