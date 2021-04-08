package dev.reactant.reactant.core

import dev.reactant.reactant.core.component.BukkitPluginContainerLoader.findAllLoadedPluginContainer
import dev.reactant.reactant.core.component.container.BukkitPluginContainer
import dev.reactant.reactant.core.component.container.ContainerManager
import dev.reactant.reactant.core.component.container.ReactantContainerManager
import dev.reactant.reactant.core.component.instance.ComponentInstanceManager
import dev.reactant.reactant.core.component.instance.ReactantInstanceManager
import dev.reactant.reactant.core.component.lifecycle.ComponentLifeCycleManager
import dev.reactant.reactant.core.component.lifecycle.ComponentLifeCycleManagerImpl
import dev.reactant.reactant.core.component.lifecycle.LifeCycleControlAction
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.TimeUnit
import kotlin.reflect.jvm.jvmName

class ReactantCore(val plugin: JavaPlugin) {
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

    fun onEnable() {

        @Suppress("UNUSED_VARIABLE")
        val metrics = Metrics(this.plugin, 6056)

        mainThreadScheduler = Schedulers.from { command: Runnable ->
            Bukkit.getServer().scheduler.runTask(this.plugin, command)
        }

        Bukkit.getServer().scheduler.scheduleSyncDelayedTask(this.plugin) {
            updateContainers()
        }

    }

    @Suppress("UNCHECKED_CAST")
    fun onPluginDisable(plugin: Plugin) {
        ReactantCore.logger.info("Disabling services")
        val container = containerManager.getContainer(BukkitPluginContainer.getIdentifier(plugin))
        if (container != null) {
            containerManager.getContainerProvidedInjectableProvider(container)
                    .mapNotNull { it as? ComponentProvider<Any> }
                    .let {
                        componentLifeCycleManager.invokeAction(it, LifeCycleControlAction.Save)
                        componentLifeCycleManager.invokeAction(it, LifeCycleControlAction.Disable)
                    }
        }
    }

    private fun updateContainers() {
        // Feed the watch dog prevent causing crash logging
        val spigotWatchDogFeeder: Disposable? = Class.forName("org.spigotmc.WatchdogThread")
                ?.let { watchDogClass ->
                    Observable.interval(50, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.newThread())
                            .subscribe {
                                watchDogClass.getMethod("tick")!!.invoke(null)
                            }
                }

        val startTime = System.currentTimeMillis()

        ReactantCore.logger.info("Searching all containers")
        findAllLoadedPluginContainer()

        ReactantCore.logger.info("Resolving service providers")
        providerManager.decideRelation()

        ReactantCore.logger.info("Initializing components")
        @Suppress("UNCHECKED_CAST")
        componentLifeCycleManager.invokeAction(
                providerManager.availableProviders
                        .mapNotNull { it as? ComponentProvider<Any> }
                        .onEach {
                            if (!it.fulfilled)
                                ReactantCore.logger.error("${it.componentClass.jvmName} missing providers: [\n${it.notFulfilledRequirements.joinToString(",\n")}\n]")
                        }
                        .filter { it.fulfilled },
                LifeCycleControlAction.Initialize)

        ReactantCore.logger.info("${ChatColor.LIGHT_PURPLE}Components Load complete! " +
                "Time used ${System.currentTimeMillis() - startTime} ms${ChatColor.RESET}")

        spigotWatchDogFeeder?.dispose()
    }

    companion object {
        @JvmStatic
        lateinit var instance: ReactantCore
            private set

        @Deprecated("Replace with EventScheduler.mainThreadScheduler")
        lateinit var mainThreadScheduler: Scheduler

        internal val logger: Logger = LogManager.getLogger("ReactantCore")
        const val configDirPath = "plugins/Reactant";
        const val tmpDirPath = "${ReactantCore.configDirPath}/tmp";

        @JvmStatic
        fun registerToLoadPath() {

        }
    }
}
