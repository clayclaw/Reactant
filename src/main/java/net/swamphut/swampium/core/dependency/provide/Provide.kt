package net.swamphut.swampium.core.dependency.provide

import kotlin.reflect.KAnnotatedElement

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class Provide(val namePattern: String = ".*") {
    companion object {
        fun fromElement(annotatedElement: KAnnotatedElement) =
                annotatedElement.annotations.filter { it is Provide }.map { (it as Provide) }.first()
    }
}
