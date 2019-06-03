package net.swamphut.swampium.core.swobject.instance.factory

import net.swamphut.swampium.core.exception.ServiceInstantiateException
import net.swamphut.swampium.core.swobject.instance.InstanceManager
import java.lang.IllegalArgumentException
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible

class SwObjectClassConstructorFactory<T : Any>(val clazz: KClass<T>, instanceManager: InstanceManager) : InstanceFactory<T> {
    override fun <T : Any> createInstance(named: String, vararg genericTypes: KClass<out Any>): T? {
        if (clazz.constructors.size != 1) throw IllegalArgumentException("SwObject must have only one constructor")
        try {
            val constructor = clazz.constructors.first();
            constructor.isAccessible = true
            constructor.parameters.map { it. }
        } catch (e: IllegalAccessException) {
            throw ServiceInstantiateException(
                    "SwObject classes are required to have a public default constructor", e, clazz)
        } catch (e: NoSuchMethodException) {
            throw ServiceInstantiateException("SwObject classes are required to have a public default constructor", e, clazz)
        } catch (e: InvocationTargetException) {
            throw ServiceInstantiateException("Exception throwed while instantiate", e, clazz)
        } catch (e: InstantiationException) {
            throw ServiceInstantiateException("Exception throwed while instantiate", e, clazz)
        }
    }
}