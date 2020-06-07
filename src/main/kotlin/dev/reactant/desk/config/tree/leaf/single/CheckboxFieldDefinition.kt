package dev.reactant.desk.config.tree.leaf.single

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.reactant.desk.config.presentation.formcontrol.CheckboxClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import dev.reactant.desk.config.tree.leaf.LeafPropertyDefinitionNode
import kotlin.reflect.KMutableProperty1

class CheckboxFieldDefinition<O>(
        parent: DefinitionBranchNode<*, *>, property: KMutableProperty1<O, Boolean>
) : LeafPropertyDefinitionNode<O, Boolean, CheckboxClientControl>(parent, property) {

    override fun convertFromJson(from: JsonElement): Boolean = from.asBoolean
    override fun convertToJson(from: Boolean): JsonElement = JsonPrimitive(from)

    override val clientControlFactory = ::CheckboxClientControl
}
