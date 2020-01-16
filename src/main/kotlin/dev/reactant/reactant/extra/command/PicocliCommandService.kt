package dev.reactant.reactant.extra.command

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.component.lifecycle.LifeCycleInspector
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import dev.reactant.reactant.extra.command.exceptions.CommandExecutionPermissionException
import dev.reactant.reactant.service.spec.dsl.Registrable
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import picocli.CommandLine.Model

@Component
class PicocliCommandService : LifeCycleHook, LifeCycleInspector, Registrable<PicocliCommandService.CommandRegistering> {
    private val argsGroupingRegex = Regex("(\"(?:\\\\\"|[^\"])+\")|((?:\\\\\"|\\\\ |[^\" ])+)");
    private val commandTreeMap = HashMap<String, CommandTree>()

    private val registerCommandNameMap = HashMap<Any, HashSet<String>>()
    private lateinit var bukkitCommandMap: SimpleCommandMap
    override fun onEnable() {
        Bukkit.getServer()::class.java.getDeclaredField("commandMap").apply {
            isAccessible = true
            bukkitCommandMap = get(Bukkit.getServer()) as SimpleCommandMap
        }
    }

    override fun beforeDisable(componentProvider: ComponentProvider<Any>) {
        registerCommandNameMap[componentProvider.getInstance()]?.forEach(this::unregisterCommand)
        registerCommandNameMap.remove(componentProvider.getInstance())
    }

    fun registerCommand(componentRegistrant: Any, commandRunnableProvider: () -> ReactantCommand): CommandTree {
        val reactantCommand = commandRunnableProvider();
        val commandSpec = Model.CommandSpec.forAnnotatedObject(reactantCommand)
        var name = commandSpec.name();
        name = reviseConflictCommand(componentRegistrant, name)

        registerCommandNameMap.getOrPut(componentRegistrant) { hashSetOf() }.add(name)
        commandTreeMap[name] = CommandTree(name, commandRunnableProvider)
        Bukkit.getHelpMap().addTopic(reactantCommand.getHelpTopic())

        bukkitCommandMap.register(componentRegistrant.javaClass.canonicalName, object : org.bukkit.command.Command(
                name,
                commandSpec.usageMessage().description().joinToString(" "),
                commandSpec.usageMessage().customSynopsis().joinToString(""),
                commandSpec.aliases().toList()) {
            override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
                val writer = CommandSenderWriter(sender)
                commandTreeMap[name]!!.getCommandLine(sender, writer)
                        .setExecutionExceptionHandler { ex, _, _ ->
                            if (ex is CommandExecutionPermissionException) {
                                sender.sendMessage("You don't have permission to do it.")
                                return@setExecutionExceptionHandler 0
                            }
                            ReactantCore.logger.error("Error occurred while executing the command \"$name ${args.joinToString(" ")}\"", ex);
                            1
                        }
                        .execute(*(argsGroupingRegex.findAll(args.joinToString(" ")).map { it.value }.toList().toTypedArray()))
                return true;
            }
        })

        return commandTreeMap[name]!!
    }

    /**
     * Revise the command with "command$number" if it have a same name with other command
     */
    private fun reviseConflictCommand(componentRegistrant: Any, name: String): String {
        var result = name;
        val existingCommand = bukkitCommandMap.commands.map { it.name }
        if (existingCommand.contains(result)) {
            val revisedNumber = (2..Int.MAX_VALUE).first { !existingCommand.contains("$result$it") }
            result = "$result$revisedNumber"
            ReactantCore.logger.warn("Command result conflict: $result " +
                    "(register by ${componentRegistrant::class.java.canonicalName}), " +
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
                    (get(bukkitCommandMap) as HashMap<String, org.bukkit.command.Command>)
                            .remove(commandName)
                }
            } catch (e: NoSuchFieldException) {

            }
        } else {
            throw IllegalArgumentException();
        }
    }

// DSL

    inner class CommandRegistering(private val componentRegistrant: Any) {
        fun command(commandProvider: () -> ReactantCommand, subRegistering: (SubCommandRegistering.() -> Unit)? = null) {
            val commandTree: CommandTree = registerCommand(componentRegistrant, commandProvider)
            subRegistering?.let { SubCommandRegistering(componentRegistrant, commandTree, commandProvider).it() }
        }
    }

    inner class SubCommandRegistering(private val componentRegistrant: Any,
                                      private val commandTree: CommandTree,
                                      private val rootCommandProvider: () -> Runnable) {
        fun command(commandProvider: () -> ReactantCommand, subRegistering: (SubCommandRegistering.() -> Unit)? = null) {
            commandTree.addSubcommand(rootCommandProvider, commandProvider)
            subRegistering?.let { SubCommandRegistering(componentRegistrant, commandTree, commandProvider).it() }
        }

        @Deprecated("Confusing name", ReplaceWith("command(commandProvider, subRegistering)"))
        fun subCommand(commandProvider: () -> ReactantCommand, subRegistering: (SubCommandRegistering.() -> Unit)? = null) =
                command(commandProvider, subRegistering)
    }

    override fun registerBy(componentRegistrant: Any, registering: CommandRegistering.() -> Unit) {
        CommandRegistering(componentRegistrant).registering()
    }
}
