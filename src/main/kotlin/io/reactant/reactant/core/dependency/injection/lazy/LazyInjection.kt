package io.reactant.reactant.core.dependency.injection.lazy

import kotlin.reflect.KType

interface LazyInjection<T : Any> {
    fun get(): T?
    val ktype: KType
}
