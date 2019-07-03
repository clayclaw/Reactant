package io.reactant.reactant.repository.commands.repository

import io.reactant.reactant.core.ReactantCore.Companion.configDirPath
import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.reactant.reactant.core.dependency.injection.Inject
import io.reactant.reactant.extra.command.PicocliCommandService
import io.reactant.reactant.repository.MavenRepositoryRetrieverService
import io.reactant.reactant.repository.RepositoryService
import io.reactant.reactant.repository.commands.RepositoryCommand
import io.reactant.reactant.repository.config.RepositoryConfig
import io.reactant.reactant.service.spec.config.Config
import io.reactant.reactant.service.spec.dsl.register

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
