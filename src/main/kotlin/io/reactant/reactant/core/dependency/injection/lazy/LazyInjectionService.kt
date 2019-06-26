package io.reactant.reactant.core.dependency.injection.lazy

import io.reactant.reactant.core.dependency.DependencyManager
import io.reactant.reactant.core.dependency.injection.Inject
import io.reactant.reactant.core.dependency.injection.Provide
import io.reactant.reactant.core.dependency.injection.producer.InjectableWrapper
import io.reactant.reactant.core.reactantobj.container.Reactant
import kotlin.reflect.KType

@Reactant
class LazyInjectionService {
    @Inject
    private lateinit var dependencyManager: DependencyManager

    @Provide(ignoreGenerics = true)
    private fun lazyInjection(ktype: KType, name: String, requester: InjectableWrapper): LazyInjection<Any> {
        return LazyInjectionImplement(dependencyManager, ktype.arguments.first().type!!, name, requester)
    }
}
