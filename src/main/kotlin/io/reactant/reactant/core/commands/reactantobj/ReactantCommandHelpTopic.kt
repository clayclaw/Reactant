package io.reactant.reactant.core.commands.reactantobj

import io.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.command.CommandSender
import org.bukkit.help.HelpTopic
import picocli.CommandLine

class ReactantCommandHelpTopic(val reactantCommand: ReactantCommand) : HelpTopic() {
    init {
        val commandSpec = CommandLine.Model.CommandSpec.forAnnotatedObject(reactantCommand)
        this.name = "/${commandSpec.name()}"
        this.shortText = commandSpec.usageMessage().description().joinToString("\n")
        this.fullText = CommandLine(reactantCommand)
                .setColorScheme(CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.OFF)).usageMessage;
    }

    override fun canSee(player: CommandSender): Boolean = reactantCommand.canSeeHelpTopic(player)

}
