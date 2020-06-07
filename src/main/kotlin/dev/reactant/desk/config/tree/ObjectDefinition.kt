package dev.reactant.desk.config.tree

import dev.reactant.desk.config.presentation.layout.PureClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import kotlin.reflect.KClass

class ObjectDefinition<T>(objectClass: KClass<*>, name: String)
    : DefinitionBranchNode<T, PureClientControl>(null, objectClass, name) {
    constructor(objectClass: KClass<*>) : this(objectClass, objectClass.simpleName ?: "Unknown")

    @Transient
    override val clientControlFactory: () -> PureClientControl = ::PureClientControl
}

