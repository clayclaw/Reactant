package dev.reactant.reactant.core.commands

import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "reactant",
    aliases = ["react", "rea"],
    mixinStandardHelpOptions = true,
    description = ["Reactant commands"]
)
internal class ReactantMainCommand : ReactantCommand(ReactantPermissions.ADMIN.DEV.toString()) {
    override fun execute() {
        requirePermission(ReactantPermissions.ADMIN.DEV)
        showUsage()
    }
}
