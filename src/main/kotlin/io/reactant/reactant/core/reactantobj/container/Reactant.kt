package io.reactant.reactant.core.reactantobj.container

import kotlin.reflect.KAnnotatedElement

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class Reactant(
        val name: String = ""
) {
    companion object {
        fun fromElement(annotatedElement: KAnnotatedElement) =
                annotatedElement.annotations.mapNotNull { it as? Reactant }.first()
    }
}
