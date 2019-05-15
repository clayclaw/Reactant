package net.swamphut.swampium.core.exception.lifecycle

import net.swamphut.swampium.core.swobject.SwObjectInfo
import net.swamphut.swampium.core.swobject.dependency.ServiceProviderInfo
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleControlAction

class RequiredServiceNotActivedException(swObjectInfo: SwObjectInfo<*>,
                                         action: LifeCycleControlAction,
                                         val notActivedServices: Collection<ServiceProviderInfo<*>>)
    : LifeCycleActionException(swObjectInfo, action) {

}
