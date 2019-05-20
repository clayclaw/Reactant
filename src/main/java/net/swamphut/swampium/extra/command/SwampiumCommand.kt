package net.swamphut.swampium.extra.command

import net.swamphut.swampium.extra.command.io.StdOut
import org.bukkit.command.CommandSender

abstract class SwampiumCommand {
    lateinit var sender: CommandSender
    lateinit var stdout: StdOut
    lateinit var stderr: StdOut
}
