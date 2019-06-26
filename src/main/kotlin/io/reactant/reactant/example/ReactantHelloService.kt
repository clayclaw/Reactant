package io.reactant.reactant.example

import io.reactant.reactant.core.reactantobj.container.Reactant
import io.reactant.reactant.core.reactantobj.lifecycle.LifeCycleHook
import org.bukkit.ChatColor
import java.util.logging.Level
import java.util.logging.Logger

@Reactant
class ReactantHelloService : LifeCycleHook, HelloService {
    private val logger = Logger.getLogger(this.javaClass.name)

    override fun onEnable() {}


    override fun sayHello(toWho: String) {
        logger.log(Level.INFO, ChatColor.AQUA.toString() + "Hello " + toWho + "!")
    }
}
