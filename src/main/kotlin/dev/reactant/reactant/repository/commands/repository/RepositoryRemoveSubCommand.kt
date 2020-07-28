package dev.reactant.reactant.repository.commands.repository

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.repository.RepositoryService
import dev.reactant.reactant.utils.PatternMatchingUtils
import picocli.CommandLine

@CommandLine.Command(name = "remove", aliases = ["rm"], mixinStandardHelpOptions = true, description = ["Remove existing repositories by name"])
class RepositoryRemoveSubCommand(private val repositoryService: RepositoryService) : ReactantCommand() {
    @CommandLine.Parameters(arity = "1..*", paramLabel = "NAME", description = ["Name of the repositories, wildcard is available"])
    lateinit var nameWildcards: ArrayList<String>

    override fun execute() {
        repositoryService.consoleOnlyValidate(sender)
        requirePermission(ReactantPermissions.REPOSITORY.MODIFY)
        repositoryService.repositoriesMap.keys
                .filter { nameWildcards.any { wildcard -> PatternMatchingUtils.matchWildcard(wildcard, it) } }
                .onEach { repositoryService.removeRepository(it) }
                .size.let { stdout.out("$it repositories found and removed successfully") }
    }
}
