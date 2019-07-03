package io.reactant.reactant.core.commands.component

import io.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
        name = "component",
        aliases = ["comp", "c"],
        mixinStandardHelpOptions = true,
        description = ["Reactant components related commands"]
)
class ComponentCommand : ReactantCommand() {
    override fun run() {
        showUsage()
    }
}
