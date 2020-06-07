package dev.reactant.desk.config.tree.leaf.single

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.reactant.desk.config.presentation.formcontrol.InputClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import dev.reactant.desk.config.tree.leaf.LeafPropertyDefinitionNode
import kotlin.reflect.KMutableProperty1

class NumberIntFieldDefinition<O>(
        parent: DefinitionBranchNode<*, *>, property: KMutableProperty1<O, Int>
) : LeafPropertyDefinitionNode<O, Int, InputClientControl>(parent, property) {
    var min: Int = Int.MIN_VALUE
    var max: Int = Int.MAX_VALUE

    override fun convertFromJson(from: JsonElement): Int = from.asInt
    override fun convertToJson(from: Int): JsonElement = JsonPrimitive(from)

    override val clientControlFactory: () -> InputClientControl = { InputClientControl("number") }
    override val clientControlModifier: InputClientControl.() -> Unit = {
        this.apply(super.clientControlModifier)
        attributes["min"] = min
        attributes["max"] = max
    }
}
