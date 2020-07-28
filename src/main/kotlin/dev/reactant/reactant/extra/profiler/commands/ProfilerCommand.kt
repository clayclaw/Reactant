package dev.reactant.reactant.extra.profiler.commands

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
        name = "profiler",
        aliases = ["prof"],
        mixinStandardHelpOptions = true,
        description = ["Reactant profiler commands"]
)
internal class ProfilerCommand : ReactantCommand() {
    override fun execute() {
        requirePermission(ReactantPermissions.ADMIN.DEV.PROFILER)
        showUsage()
    }
}
