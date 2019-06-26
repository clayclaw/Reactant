package io.reactant.reactant.core.dependency.injection.lazy

import io.reactant.reactant.core.dependency.DependencyManager
import io.reactant.reactant.core.dependency.injection.InjectRequirement
import io.reactant.reactant.core.dependency.injection.producer.InjectableWrapper
import kotlin.reflect.KType

class LazyInjectionImplement<T : Any>(val dependencyManager: DependencyManager, override val ktype: KType,
                                      val name: String, val requester: InjectableWrapper) : LazyInjection<T> {
    override fun get(): T? {
        @Suppress("UNCHECKED_CAST")
        return dependencyManager.fulfillRequirement(InjectRequirement(ktype, name))
                ?.let { it.producer(ktype, name, requester) as T }
    }


}
