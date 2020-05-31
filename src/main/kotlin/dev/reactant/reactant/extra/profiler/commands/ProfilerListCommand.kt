package dev.reactant.reactant.extra.profiler.commands

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.extra.profiler.ReactantProfilerService
import picocli.CommandLine

@CommandLine.Command(
        name = "list",
        mixinStandardHelpOptions = true,
        description = ["List running profilers"]
)
internal class ProfilerListCommand(
        private val profilerService: ReactantProfilerService
) : ReactantCommand() {
    override fun run() {
        requirePermission(ReactantPermissions.ADMIN.DEV.PROFILER)
        profilerService.runningProfilerList.forEach { stdout.out("$it") }
    }
}
