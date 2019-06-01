package net.swamphut.swampium.core.swobject.dependency.injection

import net.swamphut.swampium.core.swobject.SwObjectInfo
import net.swamphut.swampium.core.swobject.SwObjectState
import java.lang.reflect.Field

class LazyInjectionImplement<T : Any>(val swObjectInfo: SwObjectInfo<T>, val field: Field) : LazyInjection<T> {
    override fun get(): T {
        if (!isAvailable()) throw IllegalStateException("Lazy injection is not available")
        @Suppress("UNCHECKED_CAST")
        return swObjectInfo.requiredServicesResolvedResult[field.type.genericInterfaces[0]]!!.instance as T
    }

    override fun isResolved(): Boolean {
        return swObjectInfo.requiredServicesResolvedResult.get(field.type.genericInterfaces[0]) != null
    }

    override fun isAvailable(): Boolean {
        swObjectInfo.requiredServicesResolvedResult.get(field.type.genericInterfaces[0])
                .let { provider ->
                    if (provider == null || provider.state != SwObjectState.Active) return false;
                }
        return true
    }

}
