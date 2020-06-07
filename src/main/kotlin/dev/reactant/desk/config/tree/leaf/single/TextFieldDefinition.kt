package dev.reactant.desk.config.tree.leaf.single

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.reactant.desk.config.presentation.formcontrol.InputClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import dev.reactant.desk.config.tree.leaf.LeafPropertyDefinitionNode
import kotlin.reflect.KMutableProperty1

class TextFieldDefinition<O>(
        parent: DefinitionBranchNode<*, *>, property: KMutableProperty1<O, String>
) : LeafPropertyDefinitionNode<O, String, InputClientControl>(parent, property) {

    override fun convertFromJson(from: JsonElement): String = from.asString
    override fun convertToJson(from: String): JsonElement = JsonPrimitive(from)

    override val clientControlFactory: () -> InputClientControl = { InputClientControl() }
}

