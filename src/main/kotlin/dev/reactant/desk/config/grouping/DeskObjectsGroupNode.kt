package dev.reactant.desk.config.grouping

import com.google.gson.JsonObject
import dev.reactant.desk.config.grouping.dto.DeskObjectsGroupNodeSnapshot
import dev.reactant.desk.config.grouping.dto.DeskObjectsNodeSnapshot
import dev.reactant.desk.config.tree.ObjectDefinition
import dev.reactant.reactant.extra.config.type.MultiConfigs
import dev.reactant.reactant.service.spec.config.Config

class DeskObjectsGroupNode(
        identifier: String, name: String,
        parent: DeskObjectNode?) : DeskObjectNode(identifier, name, parent) {
    private val children: HashMap<String, DeskObjectNode> = hashMapOf()

    private fun checkKeyRepeat(identifier: String) {
        if (children.containsKey(identifier))
            throw IllegalArgumentException("Config node with same identifier already exist: $identifier")
    }

    fun addNode(node: DeskObjectNode) {
        checkKeyRepeat(node.identifier)
        children[node.identifier] = node
    }

    fun group(identifier: String, name: String, content: DeskObjectsGroupNode.() -> Unit) {
        addNode(DeskObjectsGroupNode(identifier, name, this@DeskObjectsGroupNode).apply(content))
    }

    inline fun <reified T : Any> multi(
            identifier: String, name: String,
            noinline configProviders: () -> Map<String, Pair<String, T>>,
            noinline defaultConfigFactory: (String) -> T,
            noinline configSaver: (String, JsonObject) -> Unit,
            definition: ObjectDefinition<T>.() -> Unit) {
        addNode(DeskMultiObjectNode(identifier, name, this, configProviders, defaultConfigFactory, configSaver,
                ObjectDefinition<T>(T::class).apply(definition)))
    }

    inline fun <reified T : Any> multiConfig(
            identifier: String, name: String,
            multiConfigs: MultiConfigs<T>,
            noinline defaultConfigFactory: (String) -> T,
            definition: ObjectDefinition<T>.() -> Unit) {
        val configGetter = { path: String -> multiConfigs.getOrDefault(path) { defaultConfigFactory(path) }.blockingGet() }
        val objectDefinition = ObjectDefinition<T>(T::class).apply(definition)
        multi(identifier, name,
                { multiConfigs.getAllAsMap(true).blockingGet().mapValues { it.key to it.value.content } },
                { path: String -> configGetter(path).content },
                { path: String, jsonObj: JsonObject ->
                    configGetter(path).also { savingConfig ->
                        objectDefinition.transferFromJsonObject(jsonObj, savingConfig.content)
                    }.save()
                }, definition)
    }

    inline fun <reified T : Any> single(
            identifier: String, name: String,
            noinline configProvider: () -> T,
            noinline objectSaver: (JsonObject) -> Unit,
            definition: ObjectDefinition<T>.() -> Unit) {
        addNode(DeskSingleObjectNode(identifier, name, this, configProvider, objectSaver, ObjectDefinition<T>(T::class).apply(definition)))
    }

    inline fun <reified T : Any> singleConfig(
            identifier: String, name: String,
            config: Config<T>,
            definition: ObjectDefinition<T>.() -> Unit) {
        val objectDefinition = ObjectDefinition<T>(T::class).apply(definition)
        single(identifier, name, { config.content },
                { jsonObj -> config.also { objectDefinition.transferFromJsonObject(jsonObj, it.content) }.save() },
                definition)
    }

    override fun getByPath(path: List<String>): DeskObjectNode? =
            if (path.isEmpty()) this else children[path.first()]?.getByPath(path.drop(1))

    override fun toSnapshot(): DeskObjectsNodeSnapshot = DeskObjectsGroupNodeSnapshot(identifier, name, children.mapValues { it.value.toSnapshot() })
}
