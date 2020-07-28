package dev.reactant.reactant.extra.profiler.commands

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import dev.reactant.reactant.extra.profiler.ReactantProfilerService
import picocli.CommandLine

@CommandLine.Command(
        name = "stop",
        mixinStandardHelpOptions = true,
        description = ["Stop the profiler"]
)
internal class ProfilerStopCommand(
        private val profilerService: ReactantProfilerService
) : ReactantCommand() {
    @CommandLine.Parameters(arity = "1", paramLabel = "PROFILER_ID",
            description = ["The profiler id you want to stop"])
    var profilerId: Int? = null

    override fun execute() {
        requirePermission(ReactantPermissions.ADMIN.DEV.PROFILER)
        if (!profilerService.runningProfilerList.contains(profilerId)) {
            stderr.out("Profiler ID $profilerId not found")
        } else {
            profilerService.stopMeasure(profilerId!!)
        }
    }
}
