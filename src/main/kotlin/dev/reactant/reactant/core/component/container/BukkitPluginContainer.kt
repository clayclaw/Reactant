package dev.reactant.reactant.core.component.container

import dev.reactant.reactant.core.ReactantPlugin
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.OptionalComponent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass

class BukkitPluginContainer(val plugin: Plugin) : Container {
    override val componentClasses: Set<KClass<out Any>>
    var _reflections: Reflections
    override val reflections: Reflections get() = _reflections

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

        Configurator.setLevel("org.reflections", Level.ERROR)
        _reflections = Reflections(ConfigurationBuilder().addUrls(servicePackagesUrl))

        val optionalComponents = reflections.getTypesAnnotatedWith(OptionalComponent::class.java)
            .filterNot {
                it.getAnnotation(OptionalComponent::class.java).requiredPlugins.any { pluginName ->
                    !Bukkit.getPluginManager().isPluginEnabled(pluginName)
                }
            }

        componentClasses = reflections.getTypesAnnotatedWith(Component::class.java)
                .union(optionalComponents)
                .map { it.kotlin }
                .toSet()
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
