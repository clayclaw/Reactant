package dev.reactant.reactant.core.commands.provider

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "provider",
    aliases = ["p"],
    mixinStandardHelpOptions = true,
    description = ["Reactant provider related commands"]
)
internal class ReactantProviderCommand : ReactantCommand(ReactantPermissions.ADMIN.DEV.OBJ.toString()) {
    override fun execute() {
        requirePermission(ReactantPermissions.ADMIN.DEV.OBJ)
        showUsage()
    }
}
