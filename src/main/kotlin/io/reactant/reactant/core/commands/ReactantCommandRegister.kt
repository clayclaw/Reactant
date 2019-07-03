package io.reactant.reactant.core.commands

import io.reactant.reactant.core.commands.component.ComponentCommand
import io.reactant.reactant.core.commands.component.ComponentListSubCommand
import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.container.ContainerManager
import io.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.reactant.reactant.core.dependency.ProviderManager
import io.reactant.reactant.core.dependency.injection.Inject
import io.reactant.reactant.extra.command.PicocliCommandService
import io.reactant.reactant.service.spec.dsl.register

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
            command(::ReactantCommand) {
                command(::ComponentCommand) {
                    command({ ComponentListSubCommand(providerManager, containerManager) })
                }
            }
        }
    }
}
