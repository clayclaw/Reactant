package dev.reactant.reactant.core.component

import kotlin.reflect.KAnnotatedElement

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class OptionalComponent(
        val requiredPlugins: Array<String>
) {
    companion object {
        fun fromElement(annotatedElement: KAnnotatedElement) =
                annotatedElement.annotations.mapNotNull { it as? OptionalComponent }.first()
    }
}
