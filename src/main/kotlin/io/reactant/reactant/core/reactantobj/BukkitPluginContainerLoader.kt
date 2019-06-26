package io.reactant.reactant.core.reactantobj

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.ReactantPlugin
import io.reactant.reactant.core.reactantobj.container.BukkitPluginContainer
import io.reactant.reactant.core.reactantobj.container.ContainerManager
import io.reactant.reactant.core.reactantobj.container.ReactantContainerManager
import org.bukkit.Bukkit
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream


object BukkitPluginContainerLoader {
    fun findAllLoadedPluginContainer() {
        val containerManager: ContainerManager = ReactantCore.instance.reactantInstanceManager
                .getOrConstructWithoutInjection(ReactantContainerManager::class)

        val out = PrintStream(FileOutputStream(FileDescriptor.out))


        val foundContainers = Bukkit.getPluginManager().plugins
                .filter { it.javaClass.isAnnotationPresent(ReactantPlugin::class.java) }
                .filter { containerManager.getContainer(BukkitPluginContainer.getIdentifier(it)) == null };

        var maxPrintLength = 0
        foundContainers.forEachIndexed { i, it ->
            "\rSearching in: ${BukkitPluginContainer.getIdentifier(it)} ...  ($i/${foundContainers.size})\r".let {
                maxPrintLength = Math.max(maxPrintLength, it.length)
                out.printf("%${maxPrintLength}s", it)
            }
            containerManager.addContainer(BukkitPluginContainer(it));
        }
    }
}

