package dev.reactant.reactant.core.component.container

import org.reflections.Reflections
import kotlin.reflect.KClass

/**
 * The container which holding the Component classes
 */
interface Container {
    val componentClasses: Set<KClass<out Any>>
    val displayName: String
    val identifier: String
    val reflections: Reflections
}
