package net.swamphut.swampium.core.swobject.container

import kotlin.reflect.KClass

interface Container {
    val swObjectClasses: Set<KClass<*>>

    val displayName: String

    val identifier: String
}
