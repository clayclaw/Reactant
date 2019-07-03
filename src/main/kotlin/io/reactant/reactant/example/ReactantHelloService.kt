package io.reactant.reactant.example

import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.lifecycle.LifeCycleHook
import org.bukkit.ChatColor
import java.util.logging.Level
import java.util.logging.Logger

@Component
class ReactantHelloService : LifeCycleHook, HelloService {
    private val logger = Logger.getLogger(this.javaClass.name)

    override fun onEnable() {}


    override fun sayHello(toWho: String) {
        logger.log(Level.INFO, ChatColor.AQUA.toString() + "Hello " + toWho + "!")
    }
}
