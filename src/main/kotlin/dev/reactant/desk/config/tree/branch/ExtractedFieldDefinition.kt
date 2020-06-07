package dev.reactant.desk.config.tree.branch

import com.google.gson.JsonElement
import dev.reactant.desk.config.presentation.layout.PureClientControl
import dev.reactant.desk.config.tree.PropertyDefinitionNode
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.jvm.jvmErasure

class ExtractedFieldDefinition<O, T>(
        parent: DefinitionBranchNode<*, *>,
        override val property: KMutableProperty1<O, T>
) : DefinitionBranchNode<T, PureClientControl>(parent, property.returnType.jvmErasure, property.name), PropertyDefinitionNode<O, T> {
    override fun convertFromJson(from: JsonElement): T = convertFromJson(from.asJsonObject)
    override val nullable: Boolean = property.returnType.isMarkedNullable

    override val clientControlFactory: () -> PureClientControl = ::PureClientControl
    override val clientControlModifier: PureClientControl.() -> Unit = {
        this.apply(super.clientControlModifier)
        this.propertyKey = this@ExtractedFieldDefinition.property.name
    }
}
