package dev.reactant.desk.config.tree.leaf.single

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.reactant.desk.config.presentation.formcontrol.InputClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import dev.reactant.desk.config.tree.leaf.LeafPropertyDefinitionNode
import kotlin.reflect.KMutableProperty1

class NumberDoubleFieldDefinition<O>(
        parent: DefinitionBranchNode<*, *>, property: KMutableProperty1<O, Double>
) : LeafPropertyDefinitionNode<O, Double, InputClientControl>(parent, property) {
    var min: Double = Double.MIN_VALUE
    var max: Double = Double.MAX_VALUE

    override fun convertFromJson(from: JsonElement): Double = from.asDouble
    override fun convertToJson(from: Double): JsonElement = JsonPrimitive(from)

    override val clientControlFactory: () -> InputClientControl = { InputClientControl("number") }
    override val clientControlModifier: InputClientControl.() -> Unit = {
        this.apply(super.clientControlModifier)
        attributes["min"] = min
        attributes["max"] = max
    }
}
