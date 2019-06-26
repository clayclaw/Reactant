package io.reactant.reactant.core.reactantobj.container

import kotlin.reflect.KClass

/**
 * The container which holding the ReactantObject classes
 */
interface Container {
    val reactantObjectClasses: Set<KClass<out Any>>

    val displayName: String

    val identifier: String
}
