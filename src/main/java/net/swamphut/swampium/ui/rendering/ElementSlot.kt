package net.swamphut.swampium.ui.rendering

import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.kits.SwUIDivElement
import net.swamphut.swampium.utils.content.item.createItemStack
import org.bukkit.inventory.ItemStack

open class ElementSlot(open val element: UIElement, open val itemStack: ItemStack) {
    companion object {
        val EMPTY = object : ElementSlot(SwUIDivElement(), createItemStack()) {
            override val element get() = throw UnsupportedOperationException("It is an empty slot")
            override val itemStack get() = throw UnsupportedOperationException("It is an empty slot")
        }
    }
}
