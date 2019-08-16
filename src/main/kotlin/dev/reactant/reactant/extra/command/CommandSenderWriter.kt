package dev.reactant.reactant.extra.command

import dev.reactant.reactant.extra.command.io.StdOutConsumer
import org.bukkit.command.CommandSender
import java.io.Writer

class CommandSenderWriter(private val commandSender: CommandSender, private val spliting: Boolean = false) : CommandSender by commandSender, Writer(), StdOutConsumer {
    override fun onOut(output: String) {
        write(output)
        flush()
    }

    private val buffer: StringBuffer = StringBuffer()
    override fun write(cbuf: CharArray?, off: Int, len: Int) {
        buffer.append(cbuf?.drop(off)?.take(len)?.toCharArray())
    }

    override fun flush() {
        synchronized(lock) {
            if (buffer.isEmpty()) return;
            if (spliting) buffer.lines()
                    .let { if (it.last().isBlank()) it.take(it.size - 1) else it } //remove last blank line
                    .forEach(commandSender::sendMessage)
            else commandSender.sendMessage(buffer.toString())
            buffer.setLength(0)
        }
    }

    override fun close() {
    }

}
