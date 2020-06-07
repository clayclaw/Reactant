package dev.reactant.desk.config.grouping

import com.google.gson.JsonObject
import dev.reactant.desk.config.grouping.dto.DeskMultiObjectsNodeSnapshot
import dev.reactant.desk.config.tree.ObjectDefinition
import dev.reactant.reactant.core.ReactantCore

class DeskMultiObjectNode<T : Any>(
        identifier: String,
        name: String,
        parent: DeskObjectNode?,
        /**
         * Provider to provide a map of key
         * Map<Config identifier, Pair<Display Name, Object>>
         * Object identifier should use "folder/subfolder/file.json" relative path style if nested
         * It must be a thread save function
         */
        val objectProviders: () -> Map<String, Pair<String, T>>,
        /**
         * Factory that use to create a default object when client requesting the default value of a new oject
         * (objectIdentifier) -> Object
         * It should not be saved since the object is not be actually created yet
         * It must be a thread save function
         */
        val defaultObjectFactory: (String) -> T,
        /**
         * The function that will be called when client requesting to save a existing object or not exist (new) object
         * (objectIdentifier, Object) -> Unit
         * It must be a thread save function
         */
        val objectSaver: (String, JsonObject) -> Unit,
        val definition: ObjectDefinition<T>
) : DeskObjectNode(identifier, name, parent) {
    val objects: Map<String, ChildObjectNode> get() = objectProviders().map { it.key to ChildObjectNode(it.key, it.value.first, this, it.value.second) }.toMap()

    inner class ChildObjectNode(identifier: String,
                                name: String,
                                parent: DeskObjectNode?, override val obj: T
    ) : DeskObjectNode(identifier, name, parent), DeskObjectEntityNode<T> {
        override fun toSnapshot() = throw UnsupportedOperationException()
        override val definition: ObjectDefinition<T> get() = this@DeskMultiObjectNode.definition
    }

    override fun getByPath(path: List<String>): DeskObjectNode? {
        ReactantCore.logger.warn("F:" + path.joinToString())
        ReactantCore.logger.warn("S:" + objects.containsKey(path.joinToString("/")))

        return if (path.isEmpty()) this else objects[path.joinToString("/")]
    }

    override fun toSnapshot() = DeskMultiObjectsNodeSnapshot(identifier, name, objects.mapValues { it.value.name })

}
