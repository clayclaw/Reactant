package dev.reactant.reactant.core.dependency.injection

import kotlin.reflect.KAnnotatedElement

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class ProvideSubtype(val namePattern: String = ".*", val ignoreGenerics: Boolean = false) {
    companion object {
        fun fromElement(annotatedElement: KAnnotatedElement) =
                annotatedElement.annotations.mapNotNull { (it as? ProvideSubtype) }.first()
    }
}
