package net.swamphut.swampium.core.commands.swobject

import net.swamphut.swampium.extra.command.SwCommand
import org.bukkit.command.CommandSender
import org.bukkit.help.HelpTopic
import picocli.CommandLine

class SwCommandHelpTopic(val swCommand: SwCommand) : HelpTopic() {
    init {
        val commandSpec = CommandLine.Model.CommandSpec.forAnnotatedObject(swCommand)
        this.name = "/${commandSpec.name()}"
        this.shortText = commandSpec.usageMessage().description().joinToString("\n")
        this.fullText = CommandLine(swCommand)
                .setColorScheme(CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.OFF)).usageMessage;
    }

    override fun canSee(player: CommandSender): Boolean = swCommand.canSeeHelpTopic(player)

}
