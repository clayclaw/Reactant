package net.swamphut.swampium.core.dependency.injection.lazy

import net.swamphut.swampium.core.dependency.DependencyManager
import net.swamphut.swampium.core.dependency.injection.InjectRequirement
import net.swamphut.swampium.core.dependency.injection.producer.InjectableWrapper
import kotlin.reflect.KType

class LazyInjectionImplement<T : Any>(val dependencyManager: DependencyManager, override val ktype: KType,
                                      val name: String, val requester: InjectableWrapper) : LazyInjection<T> {
    override fun get(): T? {
        @Suppress("UNCHECKED_CAST")
        return dependencyManager.fulfillRequirement(InjectRequirement(ktype, name))
                ?.let { it.producer(ktype, name, requester) as T }
    }


}
