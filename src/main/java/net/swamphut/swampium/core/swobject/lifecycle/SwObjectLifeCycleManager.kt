package net.swamphut.swampium.core.swobject.lifecycle

import net.swamphut.swampium.core.swobject.SwObjectInfo

interface SwObjectLifeCycleManager {
    /**
     *
     */
    fun invokeAction(swObjectInfo: SwObjectInfo<Any>, action: LifeCycleControlAction): Boolean

    /**
     * Invoke the action for all service providers
     */
    fun invokeAction(swObjectsInfo: Collection<SwObjectInfo<Any>>, action: LifeCycleControlAction): Boolean
}
