package dev.reactant.reactant.core.dependency.injection

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KType

class InjectRequirement(
        val requiredType: KType,
        val name: String
) {

    override fun toString() = "{ Type: $requiredType, Name: $name }"

    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other is InjectRequirement && other.requiredType == requiredType && other.name == name -> true
        else -> false
    }

    override fun hashCode(): Int {
        var result = requiredType.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    companion object {
        fun fromProperty(property: KMutableProperty<*>): InjectRequirement = InjectRequirement(
                property.returnType,
                extractInjectionName(property)
        )

        fun fromParameter(parameter: KParameter) = InjectRequirement(
                parameter.type,
                extractInjectionName(parameter)
        )

        private fun extractInjectionName(annotatedElement: KAnnotatedElement) =
                annotatedElement.annotations.filter { it is Inject }.map { (it as Inject).name }.firstOrNull() ?: ""
    }
}
