package net.swamphut.swampium.ui

import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.service.spec.dsl.register
import net.swamphut.swampium.service.spec.server.EventService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

@SwObject
class SwUIService(
        val event: EventService
) : LifeCycleHook {
    override fun init() {
        register(event) {
            InventoryClickEvent::class {

            }

            InventoryCloseEvent::class.listen()
        }
    }

    fun showUI(player: Player) {
        val view = Bukkit.createInventory(null, 9, "Test");
        val v = player.openInventory(view)
    }
}

