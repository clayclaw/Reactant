package io.reactant.reactant.core.component

import kotlin.reflect.KAnnotatedElement

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class Component(
        val name: String = ""
) {
    companion object {
        fun fromElement(annotatedElement: KAnnotatedElement) =
                annotatedElement.annotations.mapNotNull { it as? Component }.first()
    }
}
