package dev.reactant.reactant.ui

import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.element.style.PositioningStylePropertyValue
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.actual
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElement
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElementEditing
import dev.reactant.reactant.utils.content.item.itemStackOf
import org.bukkit.event.inventory.InventoryType.*
import org.bukkit.inventory.ItemStack

class ViewInventoryContainerElement(private val reactantUIView: ReactantUIView) : ReactantUIContainerElement("inventory") {
    override fun edit() = object : ReactantUIContainerElementEditing<ViewInventoryContainerElement>(this) {}

    override val view: UIView get() = reactantUIView

    override var parent: UIElement?
        get() = null
        set(value) = throw UnsupportedOperationException("View element cannot have parent")

    override var width: PositioningStylePropertyValue
        get() = when (view.inventory.type) {
            CHEST -> actual(9)
            DISPENSER, DROPPER, CRAFTING -> actual(3)
            else -> throw UnsupportedOperationException("Unknown inventory type: ${view.inventory.type}")
        }
        set(value) = throw java.lang.UnsupportedOperationException("View size cannot be change")

    override var height: PositioningStylePropertyValue
        get() = actual(reactantUIView.inventory.size / (width as PositioningStylePropertyValue.IntValue).value)
        set(value) = throw java.lang.UnsupportedOperationException("View size cannot be change")

    override fun getBackgroundItemStack(x: Int, y: Int): ItemStack = itemStackOf()
}

