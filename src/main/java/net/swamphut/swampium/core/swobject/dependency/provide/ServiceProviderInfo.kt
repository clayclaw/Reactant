package net.swamphut.swampium.core.swobject.dependency.provide

import net.swamphut.swampium.core.swobject.SwObjectInfo

interface ServiceProviderInfo<out T : Any> : SwObjectInfo<T> {

    /**
     * The services this provider provided
     */
    val provide: MutableSet<Class<out Any>>

    /**
     * The objects which required this provider
     */
    val requester: MutableSet<SwObjectInfo<Any>>
}
