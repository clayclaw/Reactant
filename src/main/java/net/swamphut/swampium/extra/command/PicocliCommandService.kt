package net.swamphut.swampium.extra.command

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.dependency.injection.producer.SwObjectInjectableWrapper
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.HookInspector
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.extra.command.exceptions.CommandExecutionPermissionException
import net.swamphut.swampium.service.spec.dsl.Registrable
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import picocli.CommandLine.Model
import java.util.logging.Level

@SwObject
class PicocliCommandService : LifeCycleHook, HookInspector, Registrable<PicocliCommandService.CommandRegistering> {
    private val commandTreeMap = HashMap<String, CommandTree>()

    private val registerCommandNameMap = HashMap<Any, HashSet<String>>()
    private lateinit var bukkitCommandMap: SimpleCommandMap
    override fun init() {
        Bukkit.getServer()::class.java.getDeclaredField("commandMap").apply {
            isAccessible = true
            bukkitCommandMap = get(Bukkit.getServer()) as SimpleCommandMap
        }
    }

    override fun beforeDisable(swObjectInjectableWrapper: SwObjectInjectableWrapper<Any>) {
        registerCommandNameMap[swObjectInjectableWrapper.getInstance()]?.forEach(this::unregisterCommand)
        registerCommandNameMap.remove(swObjectInjectableWrapper.getInstance())
    }

    fun registerCommand(registerSwObject: Any, commandRunnableProvider: () -> SwCommand): CommandTree {
        val swCommand = commandRunnableProvider();
        val commandSpec = Model.CommandSpec.forAnnotatedObject(swCommand)
        var name = commandSpec.name();
        name = reviseConflictCommand(registerSwObject, name)

        registerCommandNameMap.getOrPut(registerSwObject) { hashSetOf() }.add(name)
        commandTreeMap[name] = CommandTree(name, commandRunnableProvider)
        Bukkit.getHelpMap().addTopic(swCommand.getHelpTopic())

        bukkitCommandMap.register(registerSwObject.javaClass.canonicalName, object : org.bukkit.command.Command(
                name,
                commandSpec.usageMessage().description().joinToString(" "),
                commandSpec.usageMessage().customSynopsis().joinToString(""),
                commandSpec.aliases().toList()) {
            override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
                val writer = CommandSenderWriter(sender)
                commandTreeMap[name]!!.getCommandLine(sender, writer)
                        .setExecutionExceptionHandler { ex, commandLine, parseResult ->
                            if (ex is CommandExecutionPermissionException) {
                                sender.sendMessage("You don't have permission to do it.")
                                return@setExecutionExceptionHandler 0
                            }
                            Swampium.logger.error("Error occured while executing the command \"$name ${args.joinToString(" ")}\"", ex);
                            1
                        }
                        .execute(*args)
                return true;
            }
        })

        return commandTreeMap[name]!!
    }

    /**
     * Revise the command with "command$number" if it have a same name with other command
     */
    private fun reviseConflictCommand(registerSwObject: Any, name: String): String {
        var result = name;
        val existingCommand = bukkitCommandMap.commands.map { it.name }
        if (existingCommand.contains(result)) {
            val revisedNumber = (2..Int.MAX_VALUE).first { !existingCommand.contains("$result$it") }
            result = "$result$revisedNumber"
            Swampium.instance.logger.log(Level.WARNING, "Command result conflict: $result " +
                    "(register by ${registerSwObject::class.java.canonicalName}), " +
                    "revised to $result")
        }
        return result;
    }

    fun unregisterCommand(commandName: String) {
        if (commandTreeMap.containsKey(commandName)) {
            commandTreeMap.remove(commandName);
            try {
                bukkitCommandMap::class.java.getDeclaredField("knownCommands").apply {
                    isAccessible = true
                    @Suppress("UNCHECKED_CAST")
                    (get(Bukkit.getServer()) as HashMap<String, org.bukkit.command.Command>)
                            .remove(commandName)
                }
            } catch (e: NoSuchFieldException) {

            }
        } else {
            throw IllegalArgumentException();
        }
    }

// DSL

    inner class CommandRegistering(private val registerSwObject: Any) {
        fun command(commandProvider: () -> SwCommand, subRegistering: (SubCommandRegistering.() -> Unit)? = null) {
            val commandTree: CommandTree = registerCommand(registerSwObject, commandProvider)
            subRegistering?.let { SubCommandRegistering(registerSwObject, commandTree, commandProvider).it() }
        }
    }

    inner class SubCommandRegistering(private val registerSwObject: Any,
                                      private val commandTree: CommandTree,
                                      private val rootCommandProvider: () -> Runnable) {
        fun command(commandProvider: () -> SwCommand, subRegistering: (SubCommandRegistering.() -> Unit)? = null) {
            commandTree.addSubcommand(rootCommandProvider, commandProvider)
            subRegistering?.let { SubCommandRegistering(registerSwObject, commandTree, commandProvider).it() }
        }

        @Deprecated("Confusing name", ReplaceWith("command(commandProvider, subRegistering)"))
        fun subCommand(commandProvider: () -> SwCommand, subRegistering: (SubCommandRegistering.() -> Unit)? = null) =
                command(commandProvider, subRegistering)
    }

    override fun registerBy(registerSwObject: Any, registering: CommandRegistering.() -> Unit) {
        CommandRegistering(registerSwObject).registering()
    }
}
