package io.reactant.reactant.core.reactantobj.lifecycle

import io.reactant.reactant.core.dependency.injection.producer.ReactantObjectInjectableWrapper

interface HookInspector {
    fun beforeInit(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun afterInit(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun beforeSave(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun afterSave(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun beforeDisable(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun afterDisable(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {}

    fun afterBulkActionComplete(action: LifeCycleControlAction) {}
}
