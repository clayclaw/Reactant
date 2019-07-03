package io.reactant.reactant.core.dependency.injection.lazy

import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.dependency.ProviderManager
import io.reactant.reactant.core.dependency.injection.Inject
import io.reactant.reactant.core.dependency.injection.Provide
import io.reactant.reactant.core.dependency.injection.producer.Provider
import kotlin.reflect.KType

@Component
class LazyInjectionService {
    @Inject
    private lateinit var providerManager: ProviderManager

    @Provide(ignoreGenerics = true)
    private fun lazyInjection(ktype: KType, name: String, requester: Provider): LazyInjection<Any> {
        return LazyInjectionImplement(providerManager, ktype.arguments.first().type!!, name, requester)
    }
}
