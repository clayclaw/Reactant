package net.swamphut.swampium.core.swobject

import net.swamphut.swampium.core.exception.lifecycle.LifeCycleActionException
import net.swamphut.swampium.core.swobject.dependency.ServiceProviderInfo

interface SwObjectInfo<out T> {
    val instance: T

    /**
     * The required services of this object
     */
    val requiredServices: Set<Class<*>>

    /**
     * The lazy required services of this object
     */
    val lazyRequiredServices: Set<Class<*>>

    /**
     * The required service providers of this provider
     * Including both direct require and lazy require
     */
    val requiredServicesResolvedResult: MutableMap<Class<*>, ServiceProviderInfo<*>>

    /**
     * Current state of this object
     */
    var state: SwObjectState

    /**
     * Is the dependencies resolved result fulfilled
     */
    val fulfilled: Boolean
        get() = requiredServices.filter { !requiredServicesResolvedResult.containsKey(it) }.isEmpty()

    /**
     * Exception occured when invoking life cycle action
     */
    val lifeCycleActionExceptions: MutableList<LifeCycleActionException>
}
