package dev.reactant.desk.config.grouping

import com.google.gson.JsonObject
import dev.reactant.desk.config.grouping.dto.DeskObjectsNodeSnapshot
import dev.reactant.desk.config.grouping.dto.DeskSingleObjectNodeSnapshot
import dev.reactant.desk.config.tree.ObjectDefinition

class DeskSingleObjectNode<T : Any>(
        identifier: String,
        name: String,
        parent: DeskObjectNode?,
        val objectProvider: () -> T,
        val objectSaver: (JsonObject) -> Unit,
        val definition: ObjectDefinition<T>
) : DeskObjectNode(identifier, name, parent) {
    override fun toSnapshot(): DeskObjectsNodeSnapshot = DeskSingleObjectNodeSnapshot(identifier, name)
}
