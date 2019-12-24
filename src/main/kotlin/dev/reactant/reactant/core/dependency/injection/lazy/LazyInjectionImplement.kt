package dev.reactant.reactant.core.dependency.injection.lazy

import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

class LazyInjectionImplement<T : Any>(val providerManager: ProviderManager, override val ktype: KType,
                                      val name: String, val requester: Provider) : LazyInjection<T> {
    override fun get(): T? {
        @Suppress("UNCHECKED_CAST")
        return providerManager.providers.mapNotNull { it as? ComponentProvider<*> }
                .filter { it.isInitialized() }
                .filter { it.productType.isSubtypeOf(ktype) }
                .map { it.getInstance() }
                .firstOrNull() as T?
    }


}
