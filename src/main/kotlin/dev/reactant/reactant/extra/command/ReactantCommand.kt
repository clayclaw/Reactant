package dev.reactant.reactant.extra.command

import dev.reactant.reactant.extra.command.exceptions.CommandExecutionPermissionException
import dev.reactant.reactant.extra.command.io.StdOut
import org.bukkit.command.CommandSender
import org.bukkit.help.HelpTopic
import picocli.CommandLine

abstract class ReactantCommand(val helpTopicPermission: String? = null) : Runnable {

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

    fun canSeeHelpTopic(player: CommandSender): Boolean =
            helpTopicPermission == null || player.hasPermission(helpTopicPermission)

    fun getHelpTopic(): HelpTopic = ReactantCommandHelpTopic(this)
}
