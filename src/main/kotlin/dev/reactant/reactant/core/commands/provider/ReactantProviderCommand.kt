package dev.reactant.reactant.core.commands.provider

import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
        name = "provider",
        aliases = ["p"],
        mixinStandardHelpOptions = true,
        description = ["Reactant provider related commands"]
)
internal class ReactantProviderCommand : ReactantCommand() {
    override fun run() {
        showUsage()
    }
}
