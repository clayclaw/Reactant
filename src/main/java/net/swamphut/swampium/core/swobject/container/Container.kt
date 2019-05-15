package net.swamphut.swampium.core.swobject.container

interface Container {
    val swObjectClasses: Set<Class<*>>

    val displayName: String

    val identifier: String
}
