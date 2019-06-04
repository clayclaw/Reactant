package net.swamphut.swampium.core.dependency.injection

interface LazyInjection<T> {
    fun get(): T
    fun isAvailable(): Boolean
    fun isResolved(): Boolean
}
