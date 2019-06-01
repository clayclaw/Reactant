package net.swamphut.swampium.core.swobject

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.exception.lifecycle.LifeCycleActionException
import net.swamphut.swampium.core.swobject.dependency.ServiceProviderInfo

open class SwObjectInfoImpl<out T : Any>(val clazz: Class<out T>) : SwObjectInfo<T> {
    override val instance: T
        get() = Swampium.instance.instanceManager.getInstance(clazz)
    override var state: SwObjectState = SwObjectState.Unsolved
    override val requiredServices: HashSet<Class<*>> = HashSet()
    override val requiredServicesResolvedResult: HashMap<Class<*>, ServiceProviderInfo<*>> = HashMap()
    override val lazyRequiredServices: HashSet<Class<*>> = HashSet()
    override val lifeCycleActionExceptions: ArrayList<LifeCycleActionException> = ArrayList()
}
