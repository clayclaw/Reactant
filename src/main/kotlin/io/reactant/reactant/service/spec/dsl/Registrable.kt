package io.reactant.reactant.service.spec.dsl

import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.lifecycle.LifeCycleHook

interface Registrable<T> {
    fun registerBy(componentRegistrant: Any, registering: T.() -> Unit)
}

fun <T> LifeCycleHook.register(registrable: Registrable<T>, registering: T.() -> Unit) = when {
    !registering::class.java.isAnnotationPresent(Component::class.java) ->
        throw UnsupportedOperationException("Only component can use register() function.")
    else -> registrable.registerBy(this, registering)
}

