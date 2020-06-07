package dev.reactant.desk.config.grouping

import dev.reactant.desk.config.grouping.dto.DeskObjectsNodeSnapshot


abstract class DeskObjectNode(
        val identifier: String,
        val name: String,
        @Transient val parent: DeskObjectNode?
) {
    val path: List<String> get() = parent?.path ?: listOf<String>() + ""
    open fun getByPath(path: List<String>): DeskObjectNode? = if (path.isEmpty()) this else null
    abstract fun toSnapshot(): DeskObjectsNodeSnapshot
}


