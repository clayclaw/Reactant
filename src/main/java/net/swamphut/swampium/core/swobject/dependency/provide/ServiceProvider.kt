package net.swamphut.swampium.core.swobject.dependency.provide

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class ServiceProvider(
        val provide: Array<KClass<out Any>> = [],
        val name: String = ""
)
