package io.reactant.reactant.core.reactantobj.lifecycle

import io.reactant.reactant.core.dependency.injection.producer.ReactantObjectInjectableWrapper

interface ReactantObjectLifeCycleManager {
    /**
     * invoke action for a single ReactantObject
     */
    fun invokeAction(injectableWrapper: ReactantObjectInjectableWrapper<Any>, action: LifeCycleControlAction): Boolean

    /**
     * Invoke the action for all ReactantObject, order will be resolved automatically
     */
    fun invokeAction(injectables: Collection<ReactantObjectInjectableWrapper<Any>>, action: LifeCycleControlAction): Boolean
}
