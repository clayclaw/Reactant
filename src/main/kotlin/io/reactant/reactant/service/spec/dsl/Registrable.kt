package io.reactant.reactant.service.spec.dsl

import io.reactant.reactant.core.reactantobj.lifecycle.LifeCycleHook

interface Registrable<T> {
    fun registerBy(registerReactantObject: Any, registering: T.() -> Unit)
}

fun <T> LifeCycleHook.register(registrable: Registrable<T>, registering: T.() -> Unit) =
        registrable.registerBy(this, registering)
