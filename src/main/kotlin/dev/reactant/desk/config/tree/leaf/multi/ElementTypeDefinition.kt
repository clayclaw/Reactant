package dev.reactant.desk.config.tree.leaf.multi

import dev.reactant.desk.config.presentation.ClientControl
import dev.reactant.desk.config.presentation.layout.PureClientControl
import dev.reactant.desk.config.tree.DefinitionNode
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode

/**
 * Used for define generic type
 * Example: List<T>, Map<K,V>
 */
class ElementTypeDefinition<T>(var element: T)
    : DefinitionBranchNode<ElementTypeDefinition<T>, PureClientControl>(null, ElementTypeDefinition::class, "element") {

    val elementDefinition: DefinitionNode<T, ClientControl>? = null
        get() = field ?: definitions.first() as? DefinitionNode<T, ClientControl>
        ?: throw IllegalStateException("Element type not defined")

    override val clientControlFactory: () -> PureClientControl = ::PureClientControl
}
