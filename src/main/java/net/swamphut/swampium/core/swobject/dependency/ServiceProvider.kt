package net.swamphut.swampium.core.swobject.dependency

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class ServiceProvider(val provide: Array<KClass<*>> = [])
