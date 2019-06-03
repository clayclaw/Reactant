package net.swamphut.swampium.core.swobject

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.SwampiumPlugin
import net.swamphut.swampium.core.swobject.container.BukkitPluginContainer
import net.swamphut.swampium.core.swobject.container.ContainerManager
import net.swamphut.swampium.core.swobject.container.SwampiumContainerManager
import org.bukkit.Bukkit
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import kotlin.reflect.full.createType


object BukkitPluginContainerLoader {
    fun findAllLoadedPluginContainer() {
        val containerManager: ContainerManager = Swampium.instance.instanceManager
                .getInstance(SwampiumContainerManager::class.createType()) as ContainerManager

        val out = PrintStream(FileOutputStream(FileDescriptor.out))


        val foundContainers = Bukkit.getPluginManager().plugins
                .filter { it.javaClass.isAnnotationPresent(SwampiumPlugin::class.java) }
                .filter { containerManager.getContainer(BukkitPluginContainer.getIdentifier(it)) == null };

        var maxPrintLength = 0
        foundContainers.forEachIndexed { i, it ->
            "\rSearching in: ${BukkitPluginContainer.getIdentifier(it)} ...  (${i}/${foundContainers.size})\r".let {
                maxPrintLength = Math.max(maxPrintLength, it.length)
                out.printf("%${maxPrintLength}s", it)
            }
            containerManager.addContainer(BukkitPluginContainer(it));
        }
    }
}
