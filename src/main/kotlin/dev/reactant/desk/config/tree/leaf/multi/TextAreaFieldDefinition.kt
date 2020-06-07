package dev.reactant.desk.config.tree.leaf.multi

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.reactant.desk.config.presentation.formcontrol.TextAreaControlClientControl
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.jvmErasure


class TextAreaFieldDefinition<O, C : MutableCollection<String>>(
        parent: DefinitionBranchNode<*, *>,
        property: KMutableProperty1<O, C>
) : ListFieldDefinition<O, C, String, TextAreaControlClientControl>(parent, property) {
    init {
        genericT.apply { ElementTypeDefinition<String>::element text { } }
    }

    override fun convertFromJson(from: JsonElement): C {
        val result = property.returnType.jvmErasure.createInstance() as C
        from.asString.split("\n").forEach { result.add(it) }
        return result
    }

    override fun convertToJson(from: C): JsonElement {
        return JsonPrimitive(from.joinToString("\n"))
    }

    override val clientControlFactory: () -> TextAreaControlClientControl = ::TextAreaControlClientControl
}


