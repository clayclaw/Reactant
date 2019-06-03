package net.swamphut.swampium.core.swobject.instance

import net.swamphut.swampium.core.swobject.instance.factory.InstanceFactory
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface InstanceManager {
    /**
     * Get construct an identified instance by class and generic types
     */
    fun getInstance(type: KType, name: String = ""): Any?

    fun addInstanceFactory(instanceFactory: InstanceFactory);
    fun removeInstanceFactory(instanceFactory: InstanceFactory);

    fun destroyInstance(instance: Any)
}
