package io.reactant.reactant.core.component.instance

import java.util.*
import kotlin.reflect.KClass


class ReactantInstanceManager : ComponentInstanceManager {
    private val instanceMap = HashMap<KClass<out Any>, Any>();
    override fun destroyInstance(instance: Any) {
        instanceMap.remove(instance::class)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getInstance(componentClass: KClass<T>): T? {
        return instanceMap[componentClass] as T?
    }

    override fun putInstance(instance: Any) {
        instanceMap[instance::class] = instance
    }

    /**
     * This function enable Reactant core to access basic injectable object before injection services are ready
     */
    internal fun <T : Any> getOrConstructWithoutInjection(componentClass: KClass<T>): T {
        getInstance(componentClass)?.let { return it }
        if (componentClass.constructors.size == 1 && componentClass.constructors.first().parameters.isNotEmpty())
            throw IllegalStateException();
        componentClass.constructors.first().call().also {
            putInstance(it);
            return it;
        }
    }

}
