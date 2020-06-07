package dev.reactant.desk.config.tree.leaf.multi.select

import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import dev.reactant.desk.config.tree.leaf.single.select.option.ProviderOptionsContainerDefinition
import java.util.*
import kotlin.reflect.KMutableProperty1

class ProviderMultiSelectFieldDefinition<O, C : MutableCollection<T>, T>(
        parent: DefinitionBranchNode<*,*>, property: KMutableProperty1<O, C>
) : MultiSelectFieldDefinition<O, C, T, ProviderOptionsContainerDefinition.SelectFieldOption<T>>(parent, property),
        ProviderOptionsContainerDefinition<T> {

    override val options: HashMap<String, ProviderOptionsContainerDefinition.SelectFieldOption<T>> = hashMapOf()
}
