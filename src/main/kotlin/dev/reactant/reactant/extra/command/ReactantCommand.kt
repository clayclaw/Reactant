package dev.reactant.reactant.extra.command

import dev.reactant.reactant.extra.command.exceptions.CommandCommonExecutionException
import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.extra.command.exceptions.CommandExecutionActorTypeException
import dev.reactant.reactant.extra.command.exceptions.CommandExecutionPermissionException
import dev.reactant.reactant.extra.command.io.StdOut
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.help.HelpTopic
import picocli.CommandLine
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

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

    protected fun requireSenderIs(vararg senderTypes: KClass<out CommandSender>) {
        if (!senderTypes.any { it.isSuperclassOf(sender::class) })
            throw CommandExecutionActorTypeException(sender, senderTypes.toList())
    }

    protected fun requireSenderIsConsole() = requireSenderIs(ConsoleCommandSender::class)
    protected fun requireSenderIsPlayer() = requireSenderIs(Player::class)

    /**
     * The usage message of the command
     */
    protected open val usage get() = commandLine.usageMessage.lines()

    /**
     * Show the usage message to the sender with standard output
     */
    protected open fun showUsage() = usage.forEach(stdout::out)

    fun canSeeHelpTopic(player: CommandSender): Boolean =
        helpTopicPermission == null || player.hasPermission(helpTopicPermission)

    fun getHelpTopic(): HelpTopic = ReactantCommandHelpTopic(this)

    /**
     * Called when the sender execute the command
     * This function should check is there any parse errors
     */
    override fun run() {
        if (commandLine.parseResult.errors().isEmpty()) execute()
        else handleParseErrors(commandLine.parseResult.errors())
    }

    /**
     * The actual execution function of the command
     */
    abstract fun execute()

    /**
     * Called when there have parse error in the command
     */
    open fun handleParseErrors(errors: List<Exception>) {
        if (helpTopicPermission != null && !sender.hasPermission(helpTopicPermission)) {
            throw CommandExecutionPermissionException(
                sender,
                helpTopicPermission,
                "execute command: ${this.javaClass.canonicalName}"
            )
        } else {
            stderr.out(errors.first().message!!)
            usage.forEach(stderr::out)
        }
    }

    /**
     * Called when there have command internal exception occurred
     */
    open fun handleExecutionExceptions(exception: Exception, args: Array<out String>): Int {
        return if (exception is CommandCommonExecutionException) {
            stderr.out(exception.message ?: "")
            0
        } else {
            stderr.out("Error occurred while executing the command")
            ReactantCore.logger.error("Error occurred while executing the command \"${commandLine.commandSpec.name()} ${args.joinToString(" ")}\"", exception)
            1
        }
    }
}
