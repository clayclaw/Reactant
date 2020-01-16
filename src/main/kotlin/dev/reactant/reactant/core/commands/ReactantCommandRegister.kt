package dev.reactant.reactant.core.commands

import dev.reactant.reactant.core.commands.component.ReactantComponentCommand
import dev.reactant.reactant.core.commands.component.ReactantComponentListSubCommand
import dev.reactant.reactant.core.commands.provider.ReactantProviderCommand
import dev.reactant.reactant.core.commands.provider.ReactantProviderListSubCommand
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.container.ContainerManager
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.extra.command.PicocliCommandService
import dev.reactant.reactant.service.spec.dsl.register

@Component
internal class ReactantCommandRegister(
        private val commandService: PicocliCommandService,
        private val providerManager: ProviderManager,
        private val containerManager: ContainerManager
) : LifeCycleHook {

    override fun onEnable() {
        register(commandService) {
            command(::ReactantMainCommand) {
                command(::ReactantComponentCommand) {
                    command({ ReactantComponentListSubCommand(providerManager, containerManager) })
                }
                command(::ReactantEchoCommand)
                command(::ReactantProviderCommand) {
                    command({ ReactantProviderListSubCommand(providerManager, containerManager) })
                }
            }
        }
    }
}
