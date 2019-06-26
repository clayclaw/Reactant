package io.reactant.reactant.core.dependency.injection.producer

import io.reactant.reactant.core.dependency.injection.Provide
import io.reactant.reactant.core.exception.IllegalCallableInjectableProviderException
import io.reactant.reactant.utils.reflections.FieldsFinder
import kotlin.reflect.KCallable
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

/**
 * A injectable which is provided by a class function/getter
 */
class ProvideInjectableWrapper<T : Any, R : Any>(
        val providedInWrapper: ReactantObjectInjectableWrapper<T>,
        val callableFactory: KCallable<R>,
        override val ignoreGenerics: Boolean = false
) : InjectableWrapper {
    override val disabledReason: Throwable? = null
    override val productType get() = this.callableFactory.returnType
    override val namePattern get() = Provide.fromElement(callableFactory).namePattern

    override val producer: (requestedType: KType, requestedName: String, requester: InjectableWrapper) -> Any = { requestedType, requestedName, requester ->
        val provider = providedInWrapper.getInstance()
        val puttingArgs = arrayListOf(provider, requestedType, requestedName, requester)
        if (callableFactory.parameters.size > puttingArgs.size)
            throw IllegalCallableInjectableProviderException(providedInWrapper.productType.jvmErasure, callableFactory)

        (0 until callableFactory.parameters.size).forEach {
            if (!callableFactory.parameters[it].type.jvmErasure.isSuperclassOf(puttingArgs[it]::class))
                throw IllegalCallableInjectableProviderException(providedInWrapper.productType.jvmErasure, callableFactory)
        }
        callableFactory.isAccessible = true;
        callableFactory.call(*puttingArgs.take(callableFactory.parameters.size).toTypedArray());
    }

    companion object {
        fun <T : Any, R : Any> fromCallable(providedInWrapper: ReactantObjectInjectableWrapper<T>,
                                            callable: KCallable<R>) =
                ProvideInjectableWrapper(providedInWrapper, callable,
                        callable.findAnnotation<Provide>()?.ignoreGenerics
                                ?: false)

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> findAllFromReactantObjectInjectableWrapper(injectableWrapper: ReactantObjectInjectableWrapper<T>) =
                (FieldsFinder.getAllDeclaredFunctionRecursively(injectableWrapper.reactantObjectClass))
                        .filter { func -> func.annotations.any { it is Provide } }
                        .map { ProvideInjectableWrapper.fromCallable(injectableWrapper, it as KCallable<Any>) }
    }
}
