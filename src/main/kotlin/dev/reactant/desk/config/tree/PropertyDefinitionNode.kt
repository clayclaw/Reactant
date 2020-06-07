package dev.reactant.desk.config.tree

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.reactant.desk.config.presentation.ClientControl
import kotlin.reflect.KMutableProperty1

/**
 * @param O The property parent class
 * @param T The property type, also the node type
 */
interface PropertyDefinitionNode<O, T> {
    val property: KMutableProperty1<O, T>
    val nullable: Boolean

    fun convertFromJson(from: JsonElement): T
    fun convertToJson(from: T): JsonElement

    fun assignFromJsonObject(from: JsonObject, to: O) = property.set(to, convertFromJson(from[property.name]))
    fun assignToJsonObject(from: O, to: JsonObject) = to.add(property.name, convertToJson(property.get(from)))

    fun apply(from: JsonElement, to: O) = property.set(to, convertFromJson(from))

}
