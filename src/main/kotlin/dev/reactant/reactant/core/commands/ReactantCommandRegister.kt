package dev.reactant.reactant.core.commands

import dev.reactant.reactant.core.commands.component.ComponentCommand
import dev.reactant.reactant.core.commands.component.ReactantComponentListSubCommand
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.container.ContainerManager
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.command.PicocliCommandService
import dev.reactant.reactant.service.spec.dsl.register

@Component
internal class ReactantCommandRegister : LifeCycleHook {
    @Inject
    private lateinit var commandService: PicocliCommandService

    @Inject
    private lateinit var providerManager: ProviderManager

    @Inject
    private lateinit var containerManager: ContainerManager

    override fun onEnable() {
        register(commandService) {
            command(::ReactantMainCommand) {
                command(::ComponentCommand) {
                    command({ ReactantComponentListSubCommand(providerManager, containerManager) })
                }
                command(::ReactantEchoCommand)
            }
        }
    }
}
