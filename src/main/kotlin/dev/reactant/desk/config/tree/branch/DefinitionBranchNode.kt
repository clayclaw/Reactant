package dev.reactant.desk.config.tree.branch

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.reactant.desk.config.presentation.ClientControl
import dev.reactant.desk.config.tree.DefinitionNode
import dev.reactant.desk.config.tree.PropertyDefinitionNode
import dev.reactant.desk.config.tree.leaf.multi.ListItemsFieldDefinition
import dev.reactant.desk.config.tree.leaf.multi.TextAreaFieldDefinition
import dev.reactant.desk.config.tree.leaf.multi.select.ProviderMultiSelectFieldDefinition
import dev.reactant.desk.config.tree.leaf.multi.select.StaticMultiSelectFieldDefinition
import dev.reactant.desk.config.tree.leaf.single.CheckboxFieldDefinition
import dev.reactant.desk.config.tree.leaf.single.NumberDoubleFieldDefinition
import dev.reactant.desk.config.tree.leaf.single.NumberIntFieldDefinition
import dev.reactant.desk.config.tree.leaf.single.TextFieldDefinition
import dev.reactant.desk.config.tree.leaf.single.select.ProviderSelectFieldDefinition
import dev.reactant.desk.config.tree.leaf.single.select.StaticSelectFieldDefinition
import dev.reactant.reactant.core.ReactantCore
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1


abstract class DefinitionBranchNode<T, P : ClientControl>(
        parent: DefinitionBranchNode<*, *>?, objectClass: KClass<*>, val name: String
) : DefinitionNode<T, P>(parent, objectClass) {


    inline infix fun KMutableProperty1<T, String>.text(definition: TextFieldDefinition<T>.() -> Unit) {
        definitions += TextFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    inline infix fun <C : MutableCollection<String>> KMutableProperty1<T, C>.textArea(definition: TextAreaFieldDefinition<T, C>.() -> Unit) {
        definitions += TextAreaFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    inline infix fun KMutableProperty1<T, Int>.numberInt(definition: NumberIntFieldDefinition<T>.() -> Unit) {
        definitions += NumberIntFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    inline infix fun KMutableProperty1<T, Double>.numberDouble(definition: NumberDoubleFieldDefinition<T>.() -> Unit) {
        definitions += NumberDoubleFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    inline infix fun <R> KMutableProperty1<T, R>.providerSelect(definition: ProviderSelectFieldDefinition<T, R>.() -> Unit) {
        definitions += ProviderSelectFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    inline infix fun <R> KMutableProperty1<T, R>.staticSelect(definition: StaticSelectFieldDefinition<T, R>.() -> Unit) {
        definitions += StaticSelectFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    inline infix fun KMutableProperty1<T, Boolean>.checkbox(definition: CheckboxFieldDefinition<T>.() -> Unit) {
        definitions += CheckboxFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    inline infix fun expansionPanel(definition: ExpansionPanelFieldDefinition<T>.() -> Unit) {
        definitions += ExpansionPanelFieldDefinition<T>(this, this.name, this.objectClass).apply(definition)
    }

    inline infix fun <C : MutableCollection<R>, reified R> KMutableProperty1<T, C>.providerMultiSelect(definition: ProviderMultiSelectFieldDefinition<T, C, R>.() -> Unit) {
        definitions += ProviderMultiSelectFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    inline infix fun <C : MutableCollection<R>, reified R> KMutableProperty1<T, C>.staticMultiSelect(definition: StaticMultiSelectFieldDefinition<T, C, R>.() -> Unit) {
        definitions += StaticMultiSelectFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    inline infix fun <C : MutableCollection<R>, R> KMutableProperty1<T, C>.listItems(definition: ListItemsFieldDefinition<T, C, R>.() -> Unit) {
        definitions += ListItemsFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    inline infix fun <R> KMutableProperty1<T, R>.extract(definition: ExtractedFieldDefinition<T, R>.() -> Unit) {
        definitions += ExtractedFieldDefinition(this@DefinitionBranchNode, this).apply(definition)
    }

    override fun convertFromJson(from: JsonElement): T {
        TODO()
    }

    override fun convertToJson(from: T): JsonElement {
        return JsonObject().also { transferToJsonObject(from, it) }
    }

    fun transferFromJsonObject(from: JsonObject, to: T) {
        definitions.forEach { definition ->
            when (definition) {
                is PropertyDefinitionNode<*, *> -> (definition as PropertyDefinitionNode<T, *>).assignFromJsonObject(from, to)
                is DefinitionBranchNode<*, *> -> (definition as DefinitionBranchNode<T, *>).transferFromJsonObject(from, to)
                else -> throw IllegalArgumentException()
            }
        }
    }

    fun transferToJsonObject(from: T, to: JsonObject) {
        definitions.forEach { definition ->
            when (definition) {
                is PropertyDefinitionNode<*, *> -> (definition as PropertyDefinitionNode<T, *>).assignToJsonObject(from, to)
                is DefinitionBranchNode<*, *> -> (definition as DefinitionBranchNode<T, *>).transferToJsonObject(from, to)
                else -> throw IllegalArgumentException()
            }
        }
    }

    override val clientControlModifier: P.() -> Unit = {
        this.apply(super.clientControlModifier)
        this.children = definitions.map { it.toClientControl() }
    }
}
