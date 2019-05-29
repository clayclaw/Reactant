package net.swamphut.swampium.extra.command

import net.swamphut.swampium.extra.command.exceptions.CommandExecutionPermissionException
import net.swamphut.swampium.extra.command.io.StdOut
import org.bukkit.command.CommandSender
import picocli.CommandLine

abstract class SwCommand : Runnable {
    lateinit var sender: CommandSender
    lateinit var stdout: StdOut
    lateinit var stderr: StdOut
    lateinit var commandLine: CommandLine

    protected fun requirePermission(permission: PermissionNode) = requirePermission(permission.toString())

    protected fun requirePermission(permission: String) {
        if (!sender.hasPermission(permission))
            throw CommandExecutionPermissionException(sender, permission, "execute command: ${this.javaClass.canonicalName}")
    }

    protected fun showUsage() = commandLine.usageMessage.lines().forEach(stdout::out)
}
