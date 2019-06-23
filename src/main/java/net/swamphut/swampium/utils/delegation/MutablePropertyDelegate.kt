package net.swamphut.swampium.utils.delegation

import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

class MutablePropertyDelegate<T>(private val delegateTo: KMutableProperty0<T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = delegateTo.get()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = delegateTo.set(value)
}