package net.swamphut.swampium.ui.creation

import net.swamphut.swampium.ui.InventoryContainerElement
import net.swamphut.swampium.ui.SwUIContainer

class SwUICreation(private val swUIContainer: SwUIContainer) : SwUIElementCreation<InventoryContainerElement>(swUIContainer.rootElement)

fun createUI(creating: SwUICreation.() -> Unit) {
    SwUICreation(SwUIContainer()).apply(creating)
}
