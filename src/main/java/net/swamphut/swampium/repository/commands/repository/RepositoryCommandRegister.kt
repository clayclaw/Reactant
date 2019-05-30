package net.swamphut.swampium.repository.commands.repository

import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.dependency.injection.Inject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.extra.command.PicocliCommandService
import net.swamphut.swampium.repository.MavenRepositoryRetrieverService
import net.swamphut.swampium.repository.RepositoryService
import net.swamphut.swampium.repository.commands.RepositoryCommand

@SwObject
internal class RepositoryCommandRegister : LifeCycleHook {
    @Inject
    private lateinit var commandService: PicocliCommandService
    @Inject
    private lateinit var repositoryService: RepositoryService
    @Inject
    private lateinit var repositoryRetrieverService: MavenRepositoryRetrieverService

    override fun init() {
        commandService.registerBy(this) {
            command(::RepositoryCommand) {
                subCommand({ RepositoryListSubCommand(repositoryService) })
                subCommand({ RepositoryAddSubCommand(repositoryService) })
                subCommand({ RepositoryRemoveSubCommand(repositoryService) })
                subCommand({ RepositoryRetrieveSubCommand(repositoryService, repositoryRetrieverService) })
            }
        }
    }
}