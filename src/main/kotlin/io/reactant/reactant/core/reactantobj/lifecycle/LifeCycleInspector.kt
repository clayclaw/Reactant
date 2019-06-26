package io.reactant.reactant.core.reactantobj.lifecycle

import io.reactant.reactant.core.dependency.injection.producer.ReactantObjectInjectableWrapper

interface LifeCycleInspector {
    fun beforeEnable(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun afterEnable(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun beforeSave(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun afterSave(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun beforeDisable(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun afterDisable(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun afterBulkActionComplete(action: LifeCycleControlAction) {}
}
