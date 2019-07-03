package io.reactant.reactant.core.component.lifecycle

import io.reactant.reactant.core.dependency.injection.producer.ComponentProvider

interface ComponentLifeCycleManager {
    /**
     * invoke action for a single Component
     */
    fun invokeAction(injectableWrapper: ComponentProvider<Any>, action: LifeCycleControlAction): Boolean

    /**
     * Invoke the action for all Component, order will be resolved automatically
     */
    fun invokeAction(injectables: Collection<ComponentProvider<Any>>, action: LifeCycleControlAction): Boolean
}
