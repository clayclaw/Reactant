package io.reactant.reactant.core.exception

import kotlin.reflect.KCallable
import kotlin.reflect.KClass

class IllegalCallableInjectableProviderException(val clazz: KClass<out Any>, val callable: KCallable<Any>) : Exception(
        "Callable in ${clazz.qualifiedName}, name: ${callable.name}"
)