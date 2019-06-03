package net.swamphut.swampium.core.swobject.dependency

import net.swamphut.swampium.core.swobject.SwObjectInfoImpl
import net.swamphut.swampium.core.swobject.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.dependency.injection.LazyInjection
import net.swamphut.swampium.utils.reflections.FieldsFinder
import java.lang.reflect.ParameterizedType


object InjectionAnnotationReader {
    @JvmStatic
    fun read(swObjectInfo: SwObjectInfoImpl<Any>) {
        FieldsFinder.getAllDeclaredFieldsRecursively(swObjectInfo.instanceClass).asSequence()
                .filter { it.isAnnotationPresent(Inject::class.java) }
                .onEach { it.isAccessible = true }
                .forEach {
                    if (it.type == LazyInjection::class.java) {
                        swObjectInfo.lazyRequiredServices.add((it.genericType as ParameterizedType).actualTypeArguments[0].javaClass)
                    } else {
                        swObjectInfo.requiredServices.add(it.type)
                    }
                }
    }
}
