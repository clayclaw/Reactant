package dev.reactant.reactant.core.component.container

import kotlin.reflect.KClass

/**
 * The container which holding the Component classes
 */
interface Container {
    val componentClasses: Set<KClass<out Any>>

    val displayName: String

    val identifier: String
}
