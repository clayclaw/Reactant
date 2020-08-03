package dev.reactant.reactant.core.commands.completion

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.dependency.ProviderManager
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName


class ReactantInternalCommandCandidates {
    class ComponentClassNames : Iterable<String> {
        override fun iterator(): Iterator<String> = (ReactantCore.instance.instanceManager.getInstance(ProviderManager::class)?.let {
            it.providers.union(it.blacklistedProviders).map { it.productType.jvmErasure.jvmName }.toList()
        } ?: listOf()).iterator()
    }
}
