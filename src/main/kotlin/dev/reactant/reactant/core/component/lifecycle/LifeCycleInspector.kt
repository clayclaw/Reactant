package dev.reactant.reactant.core.component.lifecycle

import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider

interface LifeCycleInspector {
    fun beforeEnable(componentProvider: ComponentProvider<Any>) {}

    fun afterEnable(componentProvider: ComponentProvider<Any>) {}

    fun beforeSave(componentProvider: ComponentProvider<Any>) {}

    fun afterSave(componentProvider: ComponentProvider<Any>) {}

    fun beforeDisable(componentProvider: ComponentProvider<Any>) {}

    fun afterDisable(componentProvider: ComponentProvider<Any>) {}

    fun afterBulkActionComplete(action: LifeCycleControlAction) {}
}
