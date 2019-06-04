package net.swamphut.swampium.core.dependency.injection


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Inject(val name: String = "")
