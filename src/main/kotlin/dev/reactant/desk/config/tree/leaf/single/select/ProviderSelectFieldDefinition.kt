package dev.reactant.desk.config.tree.leaf.single.select

import dev.reactant.desk.config.presentation.formcontrol.SelectClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import dev.reactant.desk.config.tree.leaf.single.select.option.ProviderOptionsContainerDefinition
import java.util.*
import kotlin.reflect.KMutableProperty1

/**
 * Provide select options, and use factory provider to generate value when saving
 * If the value is static, you should use StaticSelectFieldDefinition instead
 */
open class ProviderSelectFieldDefinition<O, T>(
        parent: DefinitionBranchNode<*,*>, property: KMutableProperty1<O, T>
) : SelectFieldDefinition<O, T, ProviderOptionsContainerDefinition.SelectFieldOption<T>>(parent, property), ProviderOptionsContainerDefinition<T> {
    override val options: HashMap<String, ProviderOptionsContainerDefinition.SelectFieldOption<T>> = hashMapOf()
}
