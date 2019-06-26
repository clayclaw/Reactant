package io.reactant.reactant.core.commands.reactantobj

import io.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
        name = "object",
        aliases = ["obj", "o"],
        mixinStandardHelpOptions = true,
        description = ["Reactant objects related commands"]
)
class ReactantObjectCommand : ReactantCommand() {
    override fun run() {
        showUsage()
    }
}