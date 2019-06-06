package net.swamphut.swampium.core.swobject.instance

import net.swamphut.swampium.core.swobject.instance.factory.InstanceFactory
import net.swamphut.swampium.core.swobject.instance.factory.InstanceProductInfo
import java.lang.IllegalStateException
import java.util.HashMap
import kotlin.collections.HashSet
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSupertypeOf


class SwampiumInstanceManager : SwObjectInstanceManager {
    private val instanceMap = HashMap<KClass<out Any>, Any>();
    override fun destroyInstance(instance: Any) {
        instanceMap.remove(instance::class)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getInstance(swObjectClass: KClass<T>): T? {
        return instanceMap[swObjectClass] as T?
    }

    override fun putInstance(instance: Any) {
        instanceMap[instance::class] = instance
    }

    /**
     * This function enable swampium core to access basic injectable object before injection services are ready
     */
    internal fun <T : Any> getOrConstructWithoutInjection(swObjectClass: KClass<T>): T {
        if (swObjectClass.constructors.size != 1 && swObjectClass.constructors.first().parameters.isNotEmpty())
            throw IllegalStateException();
        swObjectClass.constructors.first().call().also {
            putInstance(it);
            return it;
        }
    }
}
