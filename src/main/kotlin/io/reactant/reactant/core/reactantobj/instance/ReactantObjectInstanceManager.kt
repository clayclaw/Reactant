package io.reactant.reactant.core.reactantobj.instance

import kotlin.reflect.KClass

interface ReactantObjectInstanceManager {
    /**
     * Get existing reactantobj instance
     * Return null if not yet created
     */
    fun <T : Any> getInstance(reactantObjectClass: KClass<T>): T?

    fun putInstance(instance: Any)

    fun destroyInstance(instance: Any)
}
