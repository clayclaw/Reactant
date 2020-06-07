package dev.reactant.desk.config.tree.leaf.single.select

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.reactant.desk.config.presentation.formcontrol.SelectClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import dev.reactant.desk.config.tree.leaf.LeafPropertyDefinitionNode
import dev.reactant.desk.config.tree.leaf.single.select.option.OptionsContainerDefinition
import kotlin.reflect.KMutableProperty1

abstract class SelectFieldDefinition<O, T, S : OptionsContainerDefinition.SelectFieldOption<T>>(
        parent: DefinitionBranchNode<*, *>, property: KMutableProperty1<O, T>
) : LeafPropertyDefinitionNode<O, T, SelectClientControl>(parent, property),
        OptionsContainerDefinition<T, S> {

    override fun convertFromJson(from: JsonElement): T = getOptionFromUUID(from.asString).value
    override fun convertToJson(from: T): JsonElement = JsonPrimitive(getOptionFromValue(from).id)

    override val clientControlFactory: () -> SelectClientControl = { SelectClientControl(options.mapValues { it.value.name }) }
}
