package dev.reactant.reactant.repository.commands.repository

import dev.reactant.reactant.core.ReactantCore.Companion.configDirPath
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.command.PicocliCommandService
import dev.reactant.reactant.repository.MavenRepositoryRetrieverService
import dev.reactant.reactant.repository.RepositoryService
import dev.reactant.reactant.repository.commands.RepositoryCommand
import dev.reactant.reactant.repository.config.RepositoryConfig
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.dsl.register

@Component
internal class RepositoryCommandRegister(
        private val commandService: PicocliCommandService,
        private val repositoryService: RepositoryService,
        private val repositoryRetrieverService: MavenRepositoryRetrieverService,
        @Inject(configDirPath + "/repository.json") private val repositoryConfig: Config<RepositoryConfig>
) : LifeCycleHook {
    override fun onEnable() {
        register(commandService) {
            command(::RepositoryCommand) {
                command({ RepositoryListSubCommand(repositoryService) })
                command({ RepositoryAddSubCommand(repositoryService) })
                command({ RepositoryRemoveSubCommand(repositoryService) })
                command({ RepositoryRetrieveSubCommand(repositoryService, repositoryRetrieverService) })
            }
        }
    }
}
