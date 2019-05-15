package net.swamphut.swampium.core.exception.lifecycle

import net.swamphut.swampium.core.swobject.SwObjectInfo
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleControlAction

open class LifeCycleActionException : Exception {


    val swObjectInfo: SwObjectInfo<*>
    val action: LifeCycleControlAction

    constructor(swObjectInfo: SwObjectInfo<*>, action: LifeCycleControlAction) : super() {
        this.swObjectInfo = swObjectInfo
        this.action = action
    }

    constructor(swObjectInfo: SwObjectInfo<*>, action: LifeCycleControlAction, cause: Throwable) : super(cause) {
        this.swObjectInfo = swObjectInfo
        this.action = action
    }

}
