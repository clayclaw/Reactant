package net.swamphut.swampium.core.swobject.dependency.injection

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure

class InjectRequirement(
        val requiredType: KType,
        val name: String
) {

    companion object {
        fun fromProperty(property: KMutableProperty<out Any>): InjectRequirement = InjectRequirement(
                property.returnType,
                property.annotations
                        .filter { it is Inject }
                        .map { (it as Inject).name }
                        .firstOrNull()
                        ?: ""
        )
    }
}