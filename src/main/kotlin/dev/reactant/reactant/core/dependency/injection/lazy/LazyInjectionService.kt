package dev.reactant.reactant.core.dependency.injection.lazy

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.Provide
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.dependency.layers.SystemLevel
import kotlin.reflect.KType

@Component
private class LazyInjectionService(
        private var providerManager: ProviderManager
) : SystemLevel {
    @Provide(ignoreGenerics = true)
    private fun lazyInjection(ktype: KType, name: String, requester: Provider): LazyInjection<Any> {
        return LazyInjectionImplement(providerManager, ktype.arguments.first().type!!, name, requester)
    }
}
