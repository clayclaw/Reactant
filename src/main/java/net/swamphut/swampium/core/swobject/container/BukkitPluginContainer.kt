package net.swamphut.swampium.core.swobject.container

import net.swamphut.swampium.core.SwampiumPlugin
import net.swamphut.swampium.core.dependency.provide.ServiceProvider
import org.bukkit.plugin.Plugin
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.net.URL

class BukkitPluginContainer(val plugin: Plugin) : Container {
    override val swObjectClasses: Set<Class<*>>

    private val servicePackgesUrl: Set<URL>
        get() {
            val swampiumPlugin = plugin.javaClass.getAnnotation(SwampiumPlugin::class.java)
            return swampiumPlugin.servicePackages
                    .map { urlStr -> ClasspathHelper.forPackage(urlStr, plugin.javaClass.classLoader) }
                    .flatMap { it }
                    .toSet()
        }

    init {
        if (!plugin.javaClass.isAnnotationPresent(SwampiumPlugin::class.java)) {
            throw IllegalArgumentException()
        }

        val reflections = Reflections(ConfigurationBuilder().addUrls(servicePackgesUrl))
        swObjectClasses = reflections.getTypesAnnotatedWith(SwObject::class.java)
                .union(reflections.getTypesAnnotatedWith(ServiceProvider::class.java))
    }

    override val displayName: String = plugin.description.name
    override val identifier: String = getIdentifier(plugin)

    companion object {
        @JvmStatic
        fun getIdentifier(plugin: Plugin): String {
            return "bk:${plugin.description.name}"
        }
    }
}
