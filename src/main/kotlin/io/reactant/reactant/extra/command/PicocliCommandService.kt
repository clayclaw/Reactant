package io.reactant.reactant.extra.command

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.dependency.injection.producer.ReactantObjectInjectableWrapper
import io.reactant.reactant.core.reactantobj.container.Reactant
import io.reactant.reactant.core.reactantobj.lifecycle.HookInspector
import io.reactant.reactant.core.reactantobj.lifecycle.LifeCycleHook
import io.reactant.reactant.extra.command.exceptions.CommandExecutionPermissionException
import io.reactant.reactant.service.spec.dsl.Registrable
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import picocli.CommandLine.Model
import java.util.logging.Level

@Reactant
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

    override fun beforeDisable(reactantObjectInjectableWrapper: ReactantObjectInjectableWrapper<Any>) {
        registerCommandNameMap[reactantObjectInjectableWrapper.getInstance()]?.forEach(this::unregisterCommand)
        registerCommandNameMap.remove(reactantObjectInjectableWrapper.getInstance())
    }

    fun registerCommand(registerReactantObject: Any, commandRunnableProvider: () -> ReactantCommand): CommandTree {
        val reactantCommand = commandRunnableProvider();
        val commandSpec = Model.CommandSpec.forAnnotatedObject(reactantCommand)
        var name = commandSpec.name();
        name = reviseConflictCommand(registerReactantObject, name)

        registerCommandNameMap.getOrPut(registerReactantObject) { hashSetOf() }.add(name)
        commandTreeMap[name] = CommandTree(name, commandRunnableProvider)
        Bukkit.getHelpMap().addTopic(reactantCommand.getHelpTopic())

        bukkitCommandMap.register(registerReactantObject.javaClass.canonicalName, object : org.bukkit.command.Command(
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
                            ReactantCore.logger.error("Error occured while executing the command \"$name ${args.joinToString(" ")}\"", ex);
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
    private fun reviseConflictCommand(registerReactantObject: Any, name: String): String {
        var result = name;
        val existingCommand = bukkitCommandMap.commands.map { it.name }
        if (existingCommand.contains(result)) {
            val revisedNumber = (2..Int.MAX_VALUE).first { !existingCommand.contains("$result$it") }
            result = "$result$revisedNumber"
            ReactantCore.instance.logger.log(Level.WARNING, "Command result conflict: $result " +
                    "(register by ${registerReactantObject::class.java.canonicalName}), " +
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

    inner class CommandRegistering(private val registerReactantObject: Any) {
        fun command(commandProvider: () -> ReactantCommand, subRegistering: (SubCommandRegistering.() -> Unit)? = null) {
            val commandTree: CommandTree = registerCommand(registerReactantObject, commandProvider)
            subRegistering?.let { SubCommandRegistering(registerReactantObject, commandTree, commandProvider).it() }
        }
    }

    inner class SubCommandRegistering(private val registerReactantObject: Any,
                                      private val commandTree: CommandTree,
                                      private val rootCommandProvider: () -> Runnable) {
        fun command(commandProvider: () -> ReactantCommand, subRegistering: (SubCommandRegistering.() -> Unit)? = null) {
            commandTree.addSubcommand(rootCommandProvider, commandProvider)
            subRegistering?.let { SubCommandRegistering(registerReactantObject, commandTree, commandProvider).it() }
        }

        @Deprecated("Confusing name", ReplaceWith("command(commandProvider, subRegistering)"))
        fun subCommand(commandProvider: () -> ReactantCommand, subRegistering: (SubCommandRegistering.() -> Unit)? = null) =
                command(commandProvider, subRegistering)
    }

    override fun registerBy(registerReactantObject: Any, registering: CommandRegistering.() -> Unit) {
        CommandRegistering(registerReactantObject).registering()
    }
}
