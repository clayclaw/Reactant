package io.reactant.reactant.repository.commands

import io.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(name = "repository", aliases = ["repo"], mixinStandardHelpOptions = true)
class RepositoryCommand : ReactantCommand() {
    override fun run() {
        showUsage()
    }
}