package dev.reactant.reactant.core.dependency.injection.components

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.Provide
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

@Component
private class InjectableComponentsProviderService(val providerManager: ProviderManager) {
    @Provide(".*", true)
    private fun provideComponents(kType: KType, @Suppress("UNUSED_PARAMETER") name: String): Components<Any> {
        return providerManager.availableProviders.mapNotNull { it as? ComponentProvider<*> }
                .filter { it.isInitialized() }
                .filter { it.productType.isSubtypeOf(kType.arguments[0].type!!) }
                .map { it.getInstance() }
                .let { Components(it) }
    }
}
