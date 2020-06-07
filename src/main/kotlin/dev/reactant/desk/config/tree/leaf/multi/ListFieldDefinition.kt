package dev.reactant.desk.config.tree.leaf.multi

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import dev.reactant.desk.config.presentation.ClientControl
import dev.reactant.desk.config.tree.DefinitionNode
import dev.reactant.desk.config.tree.PropertyDefinitionNode
import dev.reactant.desk.config.tree.branch.DefinitionBranchNode
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.jvmErasure

abstract class ListFieldDefinition<O, C : MutableCollection<T>, T, P : ClientControl>(
        parent: DefinitionBranchNode<*, *>,
        final override val property: KMutableProperty1<O, C>
) : DefinitionNode<C, P>(parent, property.returnType.jvmErasure),
        PropertyDefinitionNode<O, C> {

    val genericT: ElementTypeDefinition<T> = ElementTypeDefinition(property.returnType.arguments[0].type!!.jvmErasure.createInstance() as T)

    override fun convertFromJson(from: JsonElement): C {
        val result = property.returnType.jvmErasure.createInstance() as C
        from.asJsonArray.forEach { element -> result.add(genericT.elementDefinition!!.convertFromJson(element)) }
        return result
    }

    override fun convertToJson(from: C): JsonElement {
        return JsonArray().also { jsonArr ->
            from.map { element -> genericT.elementDefinition!!.convertToJson(element) }.forEach { jsonArr.add(it) }
        }
    }

    override val nullable: Boolean = property.returnType.isMarkedNullable

    override val clientControlModifier: P.() -> Unit = {
        this.apply(super.clientControlModifier)
        this.attributes["propertyKey"] = property.name
    }
}
