package dev.reactant.desk.config.tree.leaf.multi

import dev.reactant.desk.config.presentation.layout.ListClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import kotlin.reflect.KMutableProperty1

open class ListItemsFieldDefinition<O, C : MutableCollection<T>, T>(
        parent: DefinitionBranchNode<*,*>,
        property: KMutableProperty1<O, C>
) : ListFieldDefinition<O, C, T,ListClientControl>(parent, property) {

    override val clientControlFactory: () -> ListClientControl = ::ListClientControl
}


