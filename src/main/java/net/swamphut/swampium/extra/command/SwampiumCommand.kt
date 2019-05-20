package net.swamphut.swampium.extra.command

import org.bukkit.command.CommandSender
import java.io.PrintWriter

abstract class SwampiumCommand {
    lateinit var sender: CommandSender
    lateinit var stdout: PrintWriter
}