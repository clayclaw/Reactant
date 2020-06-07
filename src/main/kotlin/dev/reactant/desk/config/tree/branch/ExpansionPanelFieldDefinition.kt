package dev.reactant.desk.config.tree.branch

import dev.reactant.desk.config.presentation.layout.ExpansionPanelClientControl
import kotlin.reflect.KClass

class ExpansionPanelFieldDefinition<T>(
        parent: DefinitionBranchNode<*, *>, name: String, objectClass: KClass<*>
) : DefinitionBranchNode<T, ExpansionPanelClientControl>(parent, objectClass, name) {
    override val clientControlFactory: () -> ExpansionPanelClientControl = ::ExpansionPanelClientControl
}
