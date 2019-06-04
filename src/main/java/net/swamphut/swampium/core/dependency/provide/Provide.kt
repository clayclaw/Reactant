package net.swamphut.swampium.core.dependency.provide

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class Provide(val namePattern: String = ".*")
