package net.swamphut.swampium.core.swobject.lifecycle

import net.swamphut.swampium.core.dependency.injection.producer.SwObjectInjectableWrapper

interface SwObjectLifeCycleManager {
    /**
     * invoke action for a single SwObject
     */
    fun invokeAction(injectableWrapper: SwObjectInjectableWrapper<Any>, action: LifeCycleControlAction): Boolean

    /**
     * Invoke the action for all SwObject, order will be resolved automatically
     */
    fun invokeAction(injectables: Collection<SwObjectInjectableWrapper<Any>>, action: LifeCycleControlAction): Boolean
}
