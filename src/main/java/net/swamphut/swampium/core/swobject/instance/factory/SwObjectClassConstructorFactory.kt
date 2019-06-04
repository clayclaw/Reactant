package net.swamphut.swampium.core.swobject.instance.factory

import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.exception.SwObjectInstantiateException
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.instance.SwObjectInstanceManager
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.jvm.isAccessible

class SwObjectClassConstructorFactory<T : Any>(val clazz: KClass<T>, val swObjectInstanceManager: SwObjectInstanceManager) : InstanceFactory {
    override fun createInstance(type: KType, named: String): InstanceProductInfo? {
        if (!type.isSupertypeOf(clazz.createType()) || named != clazz.java.getDeclaredAnnotation(SwObject::class.java).name) {
            return null;
        }
        if (clazz.constructors.size != 1) throw IllegalArgumentException("SwObject must have only one constructor")
        try {
            val constructor = clazz.constructors.first();
            constructor.isAccessible = true

            val constructorParamValues = constructor.parameters.map { parameter ->
                val name = (parameter.annotations.firstOrNull { it is Inject } as Inject?)?.name ?: ""
                return@map swObjectInstanceManager.getInstance(parameter.type, name)
            }
            return InstanceProductInfo(type, named, constructor.call(constructorParamValues))
        } catch (e: InvocationTargetException) {
            throw SwObjectInstantiateException("Exception throwed while instantiate", e, clazz.java)
        } catch (e: InstantiationException) {
            throw SwObjectInstantiateException("Exception throwed while instantiate", e, clazz.java)
        }
    }
}
