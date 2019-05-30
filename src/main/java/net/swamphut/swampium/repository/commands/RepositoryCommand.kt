package net.swamphut.swampium.repository.commands

import net.swamphut.swampium.extra.command.SwCommand
import picocli.CommandLine

@CommandLine.Command(name = "repository", aliases = ["repo"], mixinStandardHelpOptions = true)
class RepositoryCommand : SwCommand() {
    override fun run() {
        showUsage()
    }
}