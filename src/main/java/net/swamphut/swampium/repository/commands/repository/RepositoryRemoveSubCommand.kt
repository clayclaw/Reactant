package net.swamphut.swampium.repository.commands.repository

import net.swamphut.swampium.extra.command.SwCommand
import net.swamphut.swampium.repository.RepositoryService
import net.swamphut.swampium.repository.commands.RepositoryPermission
import net.swamphut.swampium.utils.PatternMatchingUtils
import picocli.CommandLine

@CommandLine.Command(name = "remove", aliases = ["rm"], mixinStandardHelpOptions = true, description = ["Remove existing repositories by name"])
class RepositoryRemoveSubCommand(private val repositoryService: RepositoryService) : SwCommand() {
    @CommandLine.Parameters(arity = "1..*", paramLabel = "NAME", description = ["Name of the repositories, wildcard is available"])
    lateinit var nameWildcards: ArrayList<String>

    override fun run() {
        requirePermission(RepositoryPermission.Companion.SWAMPIUM.REPOSITORY.MODIFY)
        repositoryService.repositoriesMap.keys
                .filter { nameWildcards.any { wildcard -> PatternMatchingUtils.matchWildcard(wildcard, it) } }
                .onEach { repositoryService.removeRepository(it) }
                .size.let { stdout.out("$it repositories found and removed successfully") }
    }
}