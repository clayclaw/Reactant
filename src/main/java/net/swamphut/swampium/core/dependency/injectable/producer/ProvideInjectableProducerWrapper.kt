package net.swamphut.swampium.core.dependency.injectable.producer

import net.swamphut.swampium.core.dependency.provide.Provide
import net.swamphut.swampium.core.exception.IllegalCallableInjectableProviderException
import net.swamphut.swampium.utils.reflections.FieldsFinder
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf

/**
 * A injectable which is provided by a class function/getter
 */
class ProvideInjectableProducerWrapper<T : Any, R : Any>(
        val providedIn: KClass<T>,
        val callableFactory: KCallable<R>
) : InjectableProducerWrapper {
    var provider: T? = null

    override val productType get() = this.callableFactory.returnType
    override val namePattern get() = Provide.fromElement(callableFactory).namePattern

    override val producer: (KType, String, Any) -> Any = { requestedType, requestedName, requester ->
        val puttingArgs = arrayListOf<Any>(requestedType, requestedName, requester)
        if (callableFactory.parameters.size > puttingArgs.size)
            throw IllegalCallableInjectableProviderException(providedIn, callableFactory)

        (0..callableFactory.parameters.size).forEach {
            if (!callableFactory.parameters[it].type.isSupertypeOf(puttingArgs[it].javaClass.kotlin.createType()))
                throw IllegalCallableInjectableProviderException(providedIn, callableFactory)
        }
        callableFactory.call(providedIn, *puttingArgs.take(callableFactory.parameters.size).toTypedArray());
    }

    companion object {
        fun <T : Any, R : Any> fromCallable(clazz: KClass<T>, callable: KCallable<R>) =
                ProvideInjectableProducerWrapper(clazz, callable)

        @Suppress("UNCHECKED_CAST")
        fun findAllFromClass(clazz: KClass<Any>) {
            (FieldsFinder.getAllDeclaredFunctionRecursively(clazz) as Set<KCallable<*>>)
                    .union(FieldsFinder.getAllDeclaredPropertyRecursively(clazz) as Set<KCallable<*>>)
                    .filter { it.annotations.any { it is Provide } }
                    .map { ProvideInjectableProducerWrapper(clazz, it as KCallable<Any>) }
        }
    }
}
