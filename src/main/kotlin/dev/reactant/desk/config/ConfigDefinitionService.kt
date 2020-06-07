package dev.reactant.desk.config

import dev.reactant.desk.config.grouping.DeskObjectsGroupNode
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.example.ExampleConfig
import dev.reactant.reactant.extra.config.type.SharedConfig
import dev.reactant.reactant.service.spec.dsl.Registrable

@Component
class ConfigDefinitionService(
        @Inject("plugins/Reactant/desk-example.json")
        val example: SharedConfig<ExampleConfig>
) : LifeCycleHook, Registrable<DeskObjectsGroupNode> {

    val rootNode = DeskObjectsGroupNode("root", "Root", null)

    override fun registerBy(componentRegistrant: Any, registering: DeskObjectsGroupNode.() -> Unit) {
        rootNode.apply(registering)
    }
}

