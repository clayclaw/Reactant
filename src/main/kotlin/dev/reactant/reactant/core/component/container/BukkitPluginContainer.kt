package dev.reactant.reactant.core.component.container

import dev.reactant.reactant.core.ReactantPlugin
import dev.reactant.reactant.core.component.Component
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bukkit.plugin.Plugin
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass

class BukkitPluginContainer(val plugin: Plugin) : Container {
    override val componentClasses: Set<KClass<out Any>>

    private val servicePackagesUrl
        get() = plugin.javaClass.getAnnotation(ReactantPlugin::class.java)
                .servicePackages
                .map { urlStr -> ClasspathHelper.forPackage(urlStr, plugin.javaClass.classLoader) }
                .flatten()
                .toSet()

    init {
        if (!plugin.javaClass.isAnnotationPresent(ReactantPlugin::class.java)) {
            throw IllegalArgumentException()
        }

        Configurator.setLevel(Reflections::class.java.canonicalName, Level.ERROR)
        val reflections = Reflections(ConfigurationBuilder().addUrls(servicePackagesUrl))
        componentClasses = reflections.getTypesAnnotatedWith(Component::class.java)
                .map { it.kotlin }
                .toSet()
        Configurator.setLevel(Reflections::class.java.canonicalName, Level.INFO)
    }

    override val displayName: String = plugin.description.name
    override val identifier: String = BukkitPluginContainer.getIdentifier(plugin)

    companion object {
        @JvmStatic
        fun getIdentifier(plugin: Plugin): String {
            return "bk:${plugin.description.name}"
        }
    }
}
