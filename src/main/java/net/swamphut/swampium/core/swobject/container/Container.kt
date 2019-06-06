package net.swamphut.swampium.core.swobject.container

import kotlin.reflect.KClass

/**
 * The container which holding the SwObject classes
 */
interface Container {
    val swObjectClasses: Set<KClass<out Any>>

    val displayName: String

    val identifier: String
}
