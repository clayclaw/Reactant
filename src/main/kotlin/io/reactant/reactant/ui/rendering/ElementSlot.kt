package io.reactant.reactant.ui.rendering

import io.reactant.reactant.ui.element.UIElement
import io.reactant.reactant.ui.kits.ReactantUIDivElement
import io.reactant.reactant.utils.content.item.createItemStack
import org.bukkit.inventory.ItemStack

open class ElementSlot(open val element: UIElement, open val itemStack: ItemStack) {
    companion object {
        val EMPTY = object : ElementSlot(ReactantUIDivElement(), createItemStack()) {
            override val element get() = throw UnsupportedOperationException("It is an empty slot")
            override val itemStack get() = throw UnsupportedOperationException("It is an empty slot")
        }
    }
}
