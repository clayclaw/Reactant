package dev.reactant.reactant.core.commands

import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "echo",
    mixinStandardHelpOptions = true,
    description = ["Echo the specified string"]
)
internal class ReactantEchoCommand : ReactantCommand(ReactantPermissions.ADMIN.DEV.toString()) {
    @CommandLine.Parameters(
        arity = "1", paramLabel = "STRING",
        description = ["The message that you want to echo"]
    )
    var message: String = ""

    override fun execute() {
        requirePermission(ReactantPermissions.ADMIN.DEV)
        stdout.out(message)
    }
}
