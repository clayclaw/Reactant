package net.swamphut.swampium.core.swobject.container

import net.swamphut.swampium.core.SwampiumPlugin
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bukkit.plugin.Plugin
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass

class BukkitPluginContainer(val plugin: Plugin) : Container {
    override val swObjectClasses: Set<KClass<out Any>>

    private val servicePackagesUrl
        get() = plugin.javaClass.getAnnotation(SwampiumPlugin::class.java)
                .servicePackages
                .map { urlStr -> ClasspathHelper.forPackage(urlStr, plugin.javaClass.classLoader) }
                .flatten()
                .toSet()

    init {
        if (!plugin.javaClass.isAnnotationPresent(SwampiumPlugin::class.java)) {
            throw IllegalArgumentException()
        }

        val reflections = Reflections(ConfigurationBuilder().addUrls(servicePackagesUrl))
        Configurator.setLevel(Reflections::class.java.canonicalName, Level.ERROR)
        swObjectClasses = reflections.getTypesAnnotatedWith(SwObject::class.java)
                .map { it.kotlin }
                .toSet()
        Configurator.setLevel(Reflections::class.java.canonicalName, Level.INFO)
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
