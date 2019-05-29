package net.swamphut.swampium.extra.command

import net.swamphut.swampium.extra.command.io.StdOut
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
        val out = PrintWriter(CommandSenderWriter(writer, true));
        return constructCommandLineRecursively(rootCommandProvider, sender, StdOutImpl(writer), StdOutImpl(writer))
                .setOut(out)
                .setErr(out);
    }

    private fun constructCommandLineRecursively(commandProvider: () -> Runnable,
                                                sender: CommandSender, stdOut: StdOut, stdErr: StdOut): CommandLine {
        val commandLine = CommandLine(commandProvider().apply {
            if (this is SwCommand) {
                this.sender = sender
                this.stdout = stdOut
                this.stderr = stdErr
            }
        }).apply {
            getCommand<Runnable>().let { if (it is SwCommand) it.commandLine = this }
        };
        subCommandMap[commandProvider]?.forEach { subCommandProvider ->
            constructCommandLineRecursively(subCommandProvider,
                    sender, stdOut, stdErr).let {
                commandLine.addSubcommand(it.commandSpec.name(), it, *it.commandSpec.aliases())
            }
        }
        return commandLine
    }

    fun addSubcommand(superCommandProvider: () -> Runnable, subCommandProvider: () -> Runnable) {
        subCommandMap.getOrPut(superCommandProvider) { arrayListOf() }.add(subCommandProvider)
    }
}