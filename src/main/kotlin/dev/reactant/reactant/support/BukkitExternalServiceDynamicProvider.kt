package dev.reactant.reactant.support

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Provide
import org.bukkit.Bukkit
import org.bukkit.plugin.RegisteredServiceProvider
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

@Component
private class BukkitExternalServiceDynamicProvider {

    @Provide(".*", true)
    private fun getService(kType: KType, name: String): BukkitExternalService<*>? {
        val serviceClass = kType.arguments.first().type!!.jvmErasure
        return Bukkit.getServer().servicesManager.getRegistration(serviceClass.java)?.let { BukkitExternalServiceImpl(it) }
    }

    private class BukkitExternalServiceImpl<T>(override val bukkitRegistration: RegisteredServiceProvider<T>) : BukkitExternalService<T>
}

