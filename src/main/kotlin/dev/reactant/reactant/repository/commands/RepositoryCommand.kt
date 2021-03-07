package dev.reactant.reactant.repository.commands

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(name = "repository", aliases = ["repo"], mixinStandardHelpOptions = true)
class RepositoryCommand : ReactantCommand(ReactantPermissions.REPOSITORY.toString()) {
    override fun execute() {
        requirePermission(ReactantPermissions.REPOSITORY)
        showUsage()
    }
}
