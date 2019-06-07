package net.swamphut.swampium.example

import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import org.bukkit.ChatColor
import java.util.logging.Level
import java.util.logging.Logger

@SwObject
class SwampiumHelloService : LifeCycleHook, HelloService {
    private val logger = Logger.getLogger(this.javaClass.name)

    override fun init() {}


    override fun sayHello(toWho: String) {
        logger.log(Level.INFO, ChatColor.AQUA.toString() + "Hello " + toWho + "!")
    }
}
