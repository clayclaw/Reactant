package net.swamphut.swampium.core.dependency.injection.lazy

import net.swamphut.swampium.core.dependency.DependencyManager
import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.dependency.injection.Provide
import net.swamphut.swampium.core.dependency.injection.producer.InjectableWrapper
import net.swamphut.swampium.core.swobject.container.SwObject
import kotlin.reflect.KType

@SwObject
class LazyInjectionService {
    @Inject
    private lateinit var dependencyManager: DependencyManager

    @Provide(ignoreGenerics = true)
    private fun lazyInjection(ktype: KType, name: String, requester: InjectableWrapper): LazyInjection<Any> {
        return LazyInjectionImplement(dependencyManager, ktype.arguments.first().type!!, name, requester)
    }
}
