package dev.reactant.reactant.core.commands

import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
        name = "reactant",
        aliases = ["react", "rea"],
        mixinStandardHelpOptions = true,
        description = ["Reactant commands"]
)
class ReactantCommand : ReactantCommand() {
    override fun run() {
        showUsage()
    }
}
