package net.swamphut.swampium.core.swobject.dependency

import net.swamphut.swampium.core.swobject.SwObjectInfo

class ServiceProviderInfoImpl<T : Any>(swObjectInfo: SwObjectInfo<T>)
    : SwObjectInfo<T> by swObjectInfo, ServiceProviderInfo<T> {
    override val provide: MutableSet<Class<out Any>> = HashSet()
    override val requester: MutableSet<SwObjectInfo<Any>> = HashSet()
}
