package io.reactant.reactant.core.component.instance

import kotlin.reflect.KClass

interface ComponentInstanceManager {
    /**
     * Get existing reactantobj instance
     * Return null if not yet created
     */
    fun <T : Any> getInstance(componentClass: KClass<T>): T?

    fun putInstance(instance: Any)

    fun destroyInstance(instance: Any)
}
