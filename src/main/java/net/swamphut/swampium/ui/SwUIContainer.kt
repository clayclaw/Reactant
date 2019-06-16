package net.swamphut.swampium.ui

import org.bukkit.Bukkit

class SwUIContainer() {
    val inventory = Bukkit.createInventory(null, 9, "Test")

    val rootElement = InventoryContainerElement()

    fun render() {
    }
}
