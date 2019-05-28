package net.swamphut.swampium.extra.command

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.swobject.SwObjectInfo
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.dependency.ServiceProvider
import net.swamphut.swampium.core.swobject.lifecycle.HookInspector
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import picocli.CommandLine.Model
import java.util.logging.Level

@SwObject
@ServiceProvider
class PicocliCommandService : LifeCycleHook, HookInspector {
    private val commandTreeMap = HashMap<String, CommandTree>()

    private val registerCommandNameMap = HashMap<Any, HashSet<String>>()
    private lateinit var bukkitCommandMap: SimpleCommandMap
    override fun init() {
        Bukkit.getServer()::class.java.getDeclaredField("commandMap").apply {
            isAccessible = true
            bukkitCommandMap = get(Bukkit.getServer()) as SimpleCommandMap
        }

    }

    override fun beforeDisable(swObjectInfo: SwObjectInfo<Any>) {
        // auto unregister when disable
        registerCommandNameMap[swObjectInfo.instance]?.forEach(this::unregisterCommand)
        registerCommandNameMap.remove(swObjectInfo.instance)
    }

    fun registerCommand(registerSwObject: Any, commandRunnableProvider: () -> Runnable): CommandTree {
        val commandRunnable = commandRunnableProvider();
        val commandSpec = Model.CommandSpec.forAnnotatedObject(commandRunnable)
        var name = commandSpec.name();
        name = reviseConflictCommand(registerSwObject, name)

        registerCommandNameMap.getOrPut(registerSwObject) { hashSetOf() }.add(name)
        commandTreeMap[name] = CommandTree(name, commandRunnableProvider)

        bukkitCommandMap.register(registerSwObject.javaClass.canonicalName, object : org.bukkit.command.Command(
                name,
                commandSpec.usageMessage().description().joinToString(" "),
                commandSpec.usageMessage().customSynopsis().joinToString(""),
                commandSpec.aliases().toList()) {
            override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
                val writer = CommandSenderWriter(sender)
                commandTreeMap[name]!!.getCommandLine(sender, writer).execute(*args)
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
            bukkitCommandMap::class.java.getDeclaredField("knownCommands").apply {
                isAccessible = true
                @Suppress("UNCHECKED_CAST")
                (get(Bukkit.getServer()) as HashMap<String, org.bukkit.command.Command>)
                        .remove(commandName)
            }
        } else {
            throw IllegalArgumentException();
        }
    }

// DSL

    inner class CommandRegistering(private val registerSwObject: Any) {
        private lateinit var commandTree: CommandTree

        fun command(commandRunnableProvider: () -> Runnable, subCommandRegistering: SubCommandRegistering.() -> Unit) {
            commandTree = registerCommand(registerSwObject, commandRunnableProvider)
            SubCommandRegistering(commandRunnableProvider).subCommandRegistering()
        }

        inner class SubCommandRegistering(val rootCommandProvider: () -> Runnable)

        fun SubCommandRegistering.subCommand(subCommandRunnableProvider: () -> Runnable, subCommandRegistering: SubCommandRegistering.() -> Unit) {
            commandTree.addSubcommand(rootCommandProvider, subCommandRunnableProvider)
            SubCommandRegistering(subCommandRunnableProvider).subCommandRegistering()
        }
    }

    fun registerBy(registerSwObject: Any, registering: CommandRegistering.() -> Unit) {
        CommandRegistering(registerSwObject).registering()
    }
}
