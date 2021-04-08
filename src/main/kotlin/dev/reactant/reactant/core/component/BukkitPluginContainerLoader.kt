package dev.reactant.reactant.core.component

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.ReactantPlugin
import dev.reactant.reactant.core.component.container.ContainerManager
import dev.reactant.reactant.core.component.container.ReactantContainerManager
import dev.reactant.reactant.core.component.container.TabooPluginContainer
import io.izzel.taboolib.loader.Plugin
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream

object BukkitPluginContainerLoader {

    private val registeredPlugins: HashSet<Plugin> = hashSetOf()

    fun findAllLoadedPluginContainer() {
        val containerManager: ContainerManager = ReactantCore.instance.reactantInstanceManager
                .getOrConstructWithoutInjection(ReactantContainerManager::class)

        val out = PrintStream(FileOutputStream(FileDescriptor.out))

        var maxPrintLength = 0

        val foundContainers = registeredPlugins.filter {
            it.javaClass.isAnnotationPresent(ReactantPlugin::class.java) &&
                    containerManager.getContainer(TabooPluginContainer.getIdentifier(it)) == null
        }

        foundContainers.forEachIndexed { i, it ->
            "\rSearching in: ${TabooPluginContainer.getIdentifier(it)} ...  ($i/${foundContainers.size})\r".let {
                maxPrintLength = Math.max(maxPrintLength, it.length)
                out.printf("%${maxPrintLength}s", it)
            }
            containerManager.addContainer(TabooPluginContainer(it))
        }

    }

    fun registerPlugin(plugin: Plugin) {
        registeredPlugins.add(plugin)
    }

}

