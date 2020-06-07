package dev.reactant.desk.config.grouping

import dev.reactant.desk.config.grouping.dto.DeskConfigsNodeEditing
import dev.reactant.desk.config.tree.ObjectDefinition

interface DeskObjectEntityNode<T : Any> {
    val obj: T
    val definition: ObjectDefinition<T>

    fun toEditing(): DeskConfigsNodeEditing<T> = DeskConfigsNodeEditing(definition.toClientControl(), definition.convertToJson(obj).asJsonObject)
}
