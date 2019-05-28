package net.swamphut.swampium.extra.command

import net.swamphut.swampium.extra.command.io.StdOutImpl
import org.bukkit.command.CommandSender
import picocli.CommandLine
import java.io.PrintWriter

class CommandTree(
        val rootCommandName: String,
        val rootCommandProvider: () -> Runnable
) {
    private val subCommandMap: HashMap<() -> Runnable, ArrayList<() -> Runnable>> = hashMapOf()
    fun getCommandLine(sender: CommandSender, writer: CommandSenderWriter): CommandLine {
        val out = PrintWriter(writer)
        val commandLine = CommandLine(rootCommandProvider().apply {
            if (this is SwampiumCommand) {
                this.sender = sender
                this.stdout = StdOutImpl(writer)
                this.stderr = StdOutImpl(writer)
            }
        }).setOut(out)
        addSubCommandsIntoCommandLineRecursively(commandLine, rootCommandProvider)
        return commandLine;
    }

    private fun addSubCommandsIntoCommandLineRecursively(commandLine: CommandLine, parentCommandProvider: () -> Runnable) {
        subCommandMap[parentCommandProvider]?.forEach { subCommandProvider ->
            CommandLine(parentCommandProvider).let { subCommandCommandLine ->
                addSubCommandsIntoCommandLineRecursively(subCommandCommandLine, subCommandProvider)
                commandLine.addSubcommand(subCommandCommandLine)
            }
        }
    }

    fun addSubcommand(superCommandProvider: () -> Runnable, subCommandProvider: () -> Runnable) {
        subCommandMap.getOrPut(superCommandProvider) { arrayListOf() }.add(subCommandProvider)
    }
}