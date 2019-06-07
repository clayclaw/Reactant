package net.swamphut.swampium.core.swobject.lifecycle

import net.swamphut.swampium.core.dependency.injection.producer.SwObjectInjectableWrapper

interface HookInspector {
    fun beforeInit(swObjectInjectableWrapper: SwObjectInjectableWrapper<Any>) {}

    fun afterInit(swObjectInjectableWrapper: SwObjectInjectableWrapper<Any>) {}

    fun beforeSave(swObjectInjectableWrapper: SwObjectInjectableWrapper<Any>) {}

    fun afterSave(swObjectInjectableWrapper: SwObjectInjectableWrapper<Any>) {}

    fun beforeDisable(swObjectInjectableWrapper: SwObjectInjectableWrapper<Any>) {}

    fun afterDisable(swObjectInjectableWrapper: SwObjectInjectableWrapper<Any>) {}

    fun afterBulkActionComplete(action: LifeCycleControlAction) {}
}
