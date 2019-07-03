package io.reactant.reactant.core.dependency.injection.lazy

import io.reactant.reactant.core.dependency.ProviderManager
import io.reactant.reactant.core.dependency.injection.InjectRequirement
import io.reactant.reactant.core.dependency.injection.producer.Provider
import kotlin.reflect.KType

class LazyInjectionImplement<T : Any>(val providerManager: ProviderManager, override val ktype: KType,
                                      val name: String, val requester: Provider) : LazyInjection<T> {
    override fun get(): T? {
        @Suppress("UNCHECKED_CAST")
        return providerManager.fulfillRequirement(InjectRequirement(ktype, name))
                ?.let { it.producer(ktype, name, requester) as T }
    }


}
