package io.reactant.reactant.core.reactantobj.instance

import java.util.*
import kotlin.reflect.KClass


class ReactantInstanceManager : ReactantObjectInstanceManager {
    private val instanceMap = HashMap<KClass<out Any>, Any>();
    override fun destroyInstance(instance: Any) {
        instanceMap.remove(instance::class)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getInstance(reactantObjectClass: KClass<T>): T? {
        return instanceMap[reactantObjectClass] as T?
    }

    override fun putInstance(instance: Any) {
        instanceMap[instance::class] = instance
    }

    /**
     * This function enable Reactant core to access basic injectable object before injection services are ready
     */
    internal fun <T : Any> getOrConstructWithoutInjection(reactantObjectClass: KClass<T>): T {
        getInstance(reactantObjectClass)?.let { return it }
        if (reactantObjectClass.constructors.size == 1 && reactantObjectClass.constructors.first().parameters.isNotEmpty())
            throw IllegalStateException();
        reactantObjectClass.constructors.first().call().also {
            putInstance(it);
            return it;
        }
    }

}
