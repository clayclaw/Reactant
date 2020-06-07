package dev.reactant.desk.config.tree.leaf.multi.select

import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import dev.reactant.desk.config.tree.leaf.single.select.option.StaticOptionsContainerDefinition
import java.util.*
import kotlin.reflect.KMutableProperty1

class StaticMultiSelectFieldDefinition<O, C : MutableCollection<T>, T>(
        parent: DefinitionBranchNode<*, *>, property: KMutableProperty1<O, C>
) : MultiSelectFieldDefinition<O, C, T, StaticOptionsContainerDefinition.SelectFieldOption<T>>(parent, property),
        StaticOptionsContainerDefinition<T> {

    override val options: HashMap<String, StaticOptionsContainerDefinition.SelectFieldOption<T>> = hashMapOf()
    override val valueOptionsMap: HashMap<T, StaticOptionsContainerDefinition.SelectFieldOption<T>> = hashMapOf()
}
