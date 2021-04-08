package dev.reactant.reactant.core.component.container

import dev.reactant.reactant.core.ReactantPlugin
import dev.reactant.reactant.core.component.Component
import io.izzel.taboolib.loader.Plugin
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.reflections8.Reflections
import org.reflections8.util.ClasspathHelper
import org.reflections8.util.ConfigurationBuilder
import java.net.URL
import kotlin.reflect.KClass

class TabooPluginContainer(val plugin: Plugin) : Container {
    override val componentClasses: Set<KClass<out Any>>
    var _reflections: Reflections
    override val reflections: Reflections get() = _reflections

    private val servicePackagesUrl
        get() = plugin.javaClass.getAnnotation(ReactantPlugin::class.java)
            .servicePackages
            .map { urlStr -> ClasspathHelper.forPackage(urlStr, plugin.plugin.javaClass.classLoader) }
            .flatten()
            .toSet()

    init {
        if (!plugin.javaClass.isAnnotationPresent(ReactantPlugin::class.java)) {
            throw IllegalArgumentException("No @ReactantPlugin is found on Plugin ${plugin.javaClass.canonicalName}")
        }

        Configurator.setLevel("org.reflections", Level.ERROR)
        _reflections = Reflections(ConfigurationBuilder()
            .addUrls(servicePackagesUrl)
            .addClassLoaders(plugin.javaClass.classLoader, plugin.plugin.javaClass.classLoader)
        )

        componentClasses = reflections.getTypesAnnotatedWith(Component::class.java)
                .map { it.kotlin }
                .toSet()
    }

    override val displayName: String = plugin.plugin.description.name
    override val identifier: String = getIdentifier(plugin)

    companion object {
        @JvmStatic
        fun getIdentifier(plugin: Plugin): String {
            return "bk:${plugin.plugin.description.name}"
        }
    }
}
