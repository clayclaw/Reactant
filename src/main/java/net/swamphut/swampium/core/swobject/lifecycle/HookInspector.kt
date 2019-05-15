package net.swamphut.swampium.core.swobject.lifecycle

import net.swamphut.swampium.core.swobject.SwObjectInfo

interface HookInspector {
    fun beforeInit(swObjectInfo: SwObjectInfo<Any>) {}

    fun afterInit(swObjectInfo: SwObjectInfo<Any>) {}

    fun beforeSave(swObjectInfo: SwObjectInfo<Any>) {}

    fun afterSave(swObjectInfo: SwObjectInfo<Any>) {}

    fun beforeDisable(swObjectInfo: SwObjectInfo<Any>) {}

    fun afterDisable(swObjectInfo: SwObjectInfo<Any>) {}

    fun afterBulkActionComeplete(action: LifeCycleControlAction) {}
}
