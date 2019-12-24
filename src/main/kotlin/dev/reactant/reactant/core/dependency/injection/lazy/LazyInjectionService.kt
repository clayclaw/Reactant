package dev.reactant.reactant.core.dependency.injection.lazy

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.core.dependency.injection.Provide
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import kotlin.reflect.KType

@Component
private class LazyInjectionService {
    @Inject
    private lateinit var providerManager: ProviderManager

    @Provide(ignoreGenerics = true)
    private fun lazyInjection(ktype: KType, name: String, requester: Provider): LazyInjection<Any> {
        return LazyInjectionImplement(providerManager, ktype.arguments.first().type!!, name, requester)
    }
}
