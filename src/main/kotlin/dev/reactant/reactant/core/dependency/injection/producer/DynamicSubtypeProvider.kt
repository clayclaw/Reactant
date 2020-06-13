package dev.reactant.reactant.core.dependency.injection.producer

import dev.reactant.reactant.core.dependency.injection.ProvideSubtype
import dev.reactant.reactant.utils.reflections.FieldsFinder
import kotlin.reflect.KCallable
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

/**
 * A injectable which is provided by a class function/getter
 * In addition, it will be able to provide the subtype, generic will be ignored
 */
class DynamicSubtypeProvider<T : Any, R : Any>(
        providedInWrapper: ComponentProvider<T>,
        callableFactory: KCallable<R>
) : DynamicProvider<T, R>(providedInWrapper, callableFactory, true) {
    override val namePattern get() = ProvideSubtype.fromElement(callableFactory).namePattern

    override fun toString(): String {
        return String.format("%15s %s@%s", "Dynamic-Subtype", callableFactory.name, providedInWrapper.componentClass.qualifiedName)
    }

    override fun canProvideType(requiredKType: KType): Boolean =
            super.canProvideType(requiredKType) || requiredKType.jvmErasure.isSubclassOf(productType.jvmErasure)

    companion object {
        fun <T : Any, R : Any> fromCallable(providedInWrapper: ComponentProvider<T>, callable: KCallable<R>) =
                DynamicSubtypeProvider(providedInWrapper, callable)

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> findAllFromComponentInjectableProvider(injectableProvider: ComponentProvider<T>) =
                (FieldsFinder.getAllDeclaredFunctionRecursively(injectableProvider.componentClass))
                        .filter { func -> func.annotations.any { it is ProvideSubtype } }
                        .map { fromCallable(injectableProvider, it as KCallable<Any>) }
    }
}
