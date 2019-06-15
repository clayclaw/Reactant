package net.swamphut.swampium.service.spec.dsl

import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook

interface Registrable<T> {
    fun registerBy(registerSwObject: Any, registering: T.() -> Unit)
}

fun <T> LifeCycleHook.register(registrable: Registrable<T>, registering: T.() -> Unit) =
        registrable.registerBy(this, registering)
