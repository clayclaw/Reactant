package net.swamphut.swampium.core.swobject.instance.factory

import kotlin.reflect.KClass
import kotlin.reflect.KType

interface InstanceFactory {
    /**
     * @return null if the factory cannot create that instance
     */
    fun createInstance(type: KType, named: String): InstanceProductInfo?
}
