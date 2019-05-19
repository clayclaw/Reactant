package net.swamphut.swampium.utils.reflections

import java.lang.reflect.Field

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
}
