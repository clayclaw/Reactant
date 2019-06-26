package io.reactant.reactant.utils.reflections

import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.superclasses

object FieldsFinder {
    /**
     * call getDeclaredFields() with all inherited class recursively
     */
    fun getAllDeclaredFieldsRecursively(clazz: Class<*>, stopAt: Class<out Any>? = Object::class.java)
            : Set<Field> = when {
        stopAt != null && stopAt == clazz -> setOf()
        stopAt == null && clazz == Object::class.java -> clazz.declaredFields.toSet()
        else -> clazz.declaredFields.toHashSet().also { it.addAll(getAllDeclaredFieldsRecursively(clazz.superclass)) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getAllDeclaredPropertyRecursively(clazz: KClass<T>, stopAt: KClass<out Any>? = Object::class)
            : Set<KProperty1<out T, Any?>> = when {
        stopAt != null && stopAt == clazz -> setOf()
        stopAt == null && clazz == Object::class.java -> clazz.declaredMemberProperties.toSet()
        else -> clazz.declaredMemberProperties.toHashSet()
                .union(clazz.superclasses.flatMap { superclass -> getAllDeclaredPropertyRecursively(superclass) }) as Set<KProperty1<out T, Any?>>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getAllDeclaredFunctionRecursively(clazz: KClass<T>, stopAt: KClass<out Any>? = Object::class)
            : Set<KFunction<*>> = when {
        stopAt != null && stopAt == clazz -> setOf()
        stopAt == null && clazz == Object::class.java -> clazz.declaredMemberFunctions.toSet()
        else -> clazz.declaredMemberFunctions.toHashSet()
                .union(clazz.superclasses.flatMap { superclass -> getAllDeclaredFunctionRecursively(superclass) })
    }
}
