package net.swamphut.swampium.core.swobject

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.exception.lifecycle.LifeCycleActionException
import net.swamphut.swampium.core.swobject.dependency.provide.ServiceProviderInfo

open class SwObjectInfoImpl<out T : Any>(override val instanceClass: Class<out T>,
                                         override val fromFactory: Any?) : SwObjectInfo<T> {
    override val instance: T
        get() = Swampium.instance.instanceManager.getInstance(instanceClass)
    override var state: SwObjectState = SwObjectState.Unsolved
    override val requiredServices: HashSet<Class<*>> = HashSet()
    override val requiredServicesResolvedResult: HashMap<Class<*>, ServiceProviderInfo<*>> = HashMap()
    override val lazyRequiredServices: HashSet<Class<*>> = HashSet()
    override val lifeCycleActionExceptions: ArrayList<LifeCycleActionException> = ArrayList()
}
