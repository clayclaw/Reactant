package dev.reactant.reactant.repository.commands

import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(name = "repository", aliases = ["repo"], mixinStandardHelpOptions = true)
class RepositoryCommand : ReactantCommand() {
    override fun execute() {
        showUsage()
    }
}
