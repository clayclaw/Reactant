package net.swamphut.swampium.extra.command

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.dependency.ServiceProvider
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.extra.command.io.StdOutImpl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import picocli.CommandLine
import picocli.CommandLine.Model
import java.io.PrintWriter
import java.util.logging.Level

@SwObject
@ServiceProvider
class PicocliCommandService : LifeCycleHook {
    private val commandProviderMap = HashMap<String, () -> Runnable>()
    private lateinit var bukkitCommandMap: SimpleCommandMap
    override fun init() {
        Bukkit.getServer()::class.java.getDeclaredField("commandMap").apply {
            isAccessible = true
            bukkitCommandMap = get(Bukkit.getServer()) as SimpleCommandMap
        }

    }

    fun registerCommand(registerSwObject: Any, commandRunnableProvider: () -> Runnable) {
        //todo auto unregister
        val commandSpec = Model.CommandSpec.forAnnotatedObject(commandRunnableProvider())
        var name = commandSpec.name();
        val existingCommand = bukkitCommandMap.commands.map { it.name }
        if (existingCommand.contains(name)) {
            val revisedNumber = (2..Int.MAX_VALUE).first { !existingCommand.contains("$name$it") }
            name = "$name$revisedNumber"
            Swampium.instance.logger.log(Level.WARNING, "Command name conflict: $name " +
                    "(register by ${registerSwObject::class.java.canonicalName}), " +
                    "revised to $name")
        }
        commandProviderMap[name] = commandRunnableProvider
        bukkitCommandMap.register(registerSwObject.javaClass.canonicalName, object
            : org.bukkit.command.Command(
                name,
                commandSpec.usageMessage().description().joinToString(" "),
                commandSpec.usageMessage().customSynopsis().joinToString(""),
                commandSpec.aliases().toList()) {
            override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
                executeCommand(sender, commandRunnableProvider, args);
                return true;
            }
        })
    }

    fun executeCommand(sender: CommandSender, commandRunnableProvider: () -> Runnable, args: Array<out String>) {
        val writer = CommandSenderWriter(sender)
        val out = PrintWriter(writer)
        CommandLine(commandRunnableProvider().apply {
            if (this is SwampiumCommand) {
                this.sender = sender
                this.stdout = StdOutImpl(writer)
                this.stderr = StdOutImpl(writer)
            }
        }).setOut(out).execute(*args)
    }

    fun unregisterCommand(commandName: String) {
        if (commandProviderMap.containsKey(commandName)) {
            commandProviderMap.remove(commandName);
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
}
