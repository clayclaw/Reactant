package net.swamphut.swampium.core.swobject.dependency

import net.swamphut.swampium.core.swobject.SwObjectInfoImpl
import net.swamphut.swampium.core.swobject.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.dependency.injection.LazyInjection


object InjectionAnnotationReader {
    @JvmStatic
    fun read(swObjectInfo: SwObjectInfoImpl<Any>) {
        swObjectInfo.instance::class.java.declaredFields.asSequence()
                .filter { it.isAnnotationPresent(Inject::class.java) }
                .onEach { it.isAccessible = true }
                .map { it.type }
                .forEach {
                    if (it == LazyInjection::class.java) {
                        swObjectInfo.lazyRequiredServices.add(it.genericInterfaces[0].javaClass)
                    } else {
                        swObjectInfo.requiredServices.add(it)
                    }
                }
    }
}
