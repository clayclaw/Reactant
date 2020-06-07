package dev.reactant.desk.config.tree.leaf

import dev.reactant.desk.config.presentation.ClientControl
import dev.reactant.desk.config.tree.DefinitionNode
import dev.reactant.desk.config.tree.PropertyDefinitionNode
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.jvm.jvmErasure

abstract class LeafPropertyDefinitionNode<O, T, P : ClientControl>(
        parent: DefinitionBranchNode<*, *>?,
        @Transient final override val property: KMutableProperty1<O, T>
) : DefinitionNode<T, P>(parent, property.returnType.jvmErasure), PropertyDefinitionNode<O, T> {

    init {
        this.displayName = property.name
    }

    override val nullable: Boolean = property.returnType.isMarkedNullable

    override val clientControlModifier: P.() -> Unit = {
        this.apply(super.clientControlModifier)
        this.attributes["propertyKey"] = property.name
    }
}

