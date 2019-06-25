package net.swamphut.swampium.core.commands.swobject

import net.swamphut.swampium.extra.command.SwCommand
import org.bukkit.entity.Player
import picocli.CommandLine

@CommandLine.Command(
        name = "swobject",
        aliases = ["swo", "swobj"],
        mixinStandardHelpOptions = true,
        description = ["SwObjects related commands"]
)
class SwObjectCommand : SwCommand() {
    override fun run() {
        showUsage()
    }
}