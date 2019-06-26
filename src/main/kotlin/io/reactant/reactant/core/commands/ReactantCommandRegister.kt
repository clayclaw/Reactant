package io.reactant.reactant.core.commands

import io.reactant.reactant.core.commands.reactantobj.ReactantObjectCommand
import io.reactant.reactant.core.commands.reactantobj.ReactantObjectListSubCommand
import io.reactant.reactant.core.dependency.DependencyManager
import io.reactant.reactant.core.dependency.injection.Inject
import io.reactant.reactant.core.reactantobj.container.ContainerManager
import io.reactant.reactant.core.reactantobj.container.Reactant
import io.reactant.reactant.core.reactantobj.lifecycle.LifeCycleHook
import io.reactant.reactant.extra.command.PicocliCommandService
import io.reactant.reactant.service.spec.dsl.register

@Reactant
internal class ReactantCommandRegister : LifeCycleHook {
    @Inject
    private lateinit var commandService: PicocliCommandService

    @Inject
    private lateinit var dependencyManager: DependencyManager

    @Inject
    private lateinit var containerManager: ContainerManager

    override fun init() {
        register(commandService) {
            command(::ReactantCommand) {
                command(::ReactantObjectCommand) {
                    command({ ReactantObjectListSubCommand(dependencyManager, containerManager) })
                }
            }
        }
    }
}
