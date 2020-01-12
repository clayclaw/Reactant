package dev.reactant.reactant.support

import org.bukkit.plugin.RegisteredServiceProvider

interface BukkitExternalService<T> : ExternalService<T> {
    val bukkitRegistration: RegisteredServiceProvider<T>
    override val provider get() = bukkitRegistration.provider
}
