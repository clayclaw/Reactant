package dev.reactant.reactant.extra.i18n.commands

import dev.reactant.reactant.core.commands.ReactantPermissions
import dev.reactant.reactant.extra.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
        name = "i18n",
        mixinStandardHelpOptions = true,
        description = ["I18n related commands"]
)
internal class I18nCommand : ReactantCommand() {
    override fun run() {
        requirePermission(ReactantPermissions.ADMIN.DEV.I18N)
        showUsage()
    }
}
