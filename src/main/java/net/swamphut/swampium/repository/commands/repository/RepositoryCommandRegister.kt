package net.swamphut.swampium.repository.commands.repository

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.extra.command.PicocliCommandService
import net.swamphut.swampium.repository.MavenRepositoryRetrieverService
import net.swamphut.swampium.repository.RepositoryService
import net.swamphut.swampium.repository.commands.RepositoryCommand
import net.swamphut.swampium.repository.config.RepositoryConfig
import net.swamphut.swampium.service.spec.config.Config
import net.swamphut.swampium.service.spec.dsl.register

@SwObject
internal class RepositoryCommandRegister(
        private val commandService: PicocliCommandService,
        private val repositoryService: RepositoryService,
        private val repositoryRetrieverService: MavenRepositoryRetrieverService,
        @Inject(Swampium.configDirPath + "/repository.json") private val repositoryConfig: Config<RepositoryConfig>
) : LifeCycleHook {
    override fun init() {
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
