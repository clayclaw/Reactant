package net.swamphut.swampium.extra.command

import org.bukkit.command.CommandSender
import java.io.Writer

class CommandSenderWriter(private val commandSender: CommandSender) : CommandSender by commandSender, Writer() {
    private val buffer: StringBuffer = StringBuffer()
    override fun write(cbuf: CharArray?, off: Int, len: Int) {
        buffer.append(cbuf?.drop(off)?.take(len)?.toCharArray())
    }

    override fun flush() {
        synchronized(lock) {
            if (buffer.isEmpty()) return;
            commandSender.sendMessage(buffer.toString())
            buffer.setLength(0)
        }
    }

    override fun close() {
    }

}