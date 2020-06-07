package dev.reactant.desk.config.tree.leaf.multi.select

import dev.reactant.desk.config.presentation.formcontrol.SelectClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import dev.reactant.desk.config.tree.leaf.multi.ListFieldDefinition
import dev.reactant.desk.config.tree.leaf.single.select.option.OptionsContainerDefinition
import kotlin.reflect.KMutableProperty1

abstract class MultiSelectFieldDefinition<O, C : MutableCollection<T>, T, S : OptionsContainerDefinition.SelectFieldOption<T>>(
        parent: DefinitionBranchNode<*, *>, property: KMutableProperty1<O, C>
) : ListFieldDefinition<O, C, T, SelectClientControl>(parent, property), OptionsContainerDefinition<T, S> {

    override val clientControlFactory: () -> SelectClientControl = { SelectClientControl(options.mapValues { it.value.name }) }
}
