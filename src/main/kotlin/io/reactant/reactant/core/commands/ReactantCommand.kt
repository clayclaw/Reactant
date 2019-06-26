package io.reactant.reactant.core.commands

import io.reactant.reactant.extra.command.ReactantCommand
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