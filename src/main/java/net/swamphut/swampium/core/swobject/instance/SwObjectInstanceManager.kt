package net.swamphut.swampium.core.swobject.instance

import kotlin.reflect.KClass

interface SwObjectInstanceManager {
    /**
     * Get existing swobject instance
     * Return null if not yet created
     */
    fun <T : Any> getInstance(swObjectClass: KClass<T>): T?

    fun putInstance(instance: Any)

    fun destroyInstance(instance: Any)
}
