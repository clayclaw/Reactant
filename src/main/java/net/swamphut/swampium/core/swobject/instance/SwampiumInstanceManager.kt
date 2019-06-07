package net.swamphut.swampium.core.swobject.instance

import java.util.*
import kotlin.reflect.KClass


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
        getInstance(swObjectClass)?.let { return it }
        if (swObjectClass.constructors.size == 1 && swObjectClass.constructors.first().parameters.isNotEmpty())
            throw IllegalStateException();
        swObjectClass.constructors.first().call().also {
            putInstance(it);
            return it;
        }
    }

}
