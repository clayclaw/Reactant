package dev.reactant.reactant.service.spec.dsl

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook

@Deprecated("Registant is redundant")
interface Registrable<T> {
    fun registerBy(componentRegistrant: Any, registering: T.() -> Unit)
}

@Deprecated("Registant is redundant")
fun <T> LifeCycleHook.register(registrable: Registrable<T>, registering: T.() -> Unit) = when {
    !this.javaClass.isAnnotationPresent(Component::class.java) ->
        throw UnsupportedOperationException("Only component can use register() function.")
    else -> registrable.registerBy(this, registering)
}