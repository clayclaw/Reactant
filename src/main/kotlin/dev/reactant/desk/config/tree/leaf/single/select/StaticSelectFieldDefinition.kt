package dev.reactant.desk.config.tree.leaf.single.select

import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import dev.reactant.desk.config.tree.leaf.single.select.option.StaticOptionsContainerDefinition
import java.util.*
import kotlin.reflect.KMutableProperty1

open class StaticSelectFieldDefinition<O, T>(
        parent: DefinitionBranchNode<*, *>, property: KMutableProperty1<O, T>
) : SelectFieldDefinition<O, T, StaticOptionsContainerDefinition.SelectFieldOption<T>>(parent, property), StaticOptionsContainerDefinition<T> {
    override val options: HashMap<String, StaticOptionsContainerDefinition.SelectFieldOption<T>> = hashMapOf()

    @Transient
    override val valueOptionsMap: HashMap<T, StaticOptionsContainerDefinition.SelectFieldOption<T>> = hashMapOf()
}
