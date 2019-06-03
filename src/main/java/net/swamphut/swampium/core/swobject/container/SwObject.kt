package net.swamphut.swampium.core.swobject.container

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class SwObject(
        val name: String = ""
)
