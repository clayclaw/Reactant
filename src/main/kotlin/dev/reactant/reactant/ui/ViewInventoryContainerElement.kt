package dev.reactant.reactant.ui

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.PositioningIntValue
import dev.reactant.reactant.ui.element.style.actual
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElement
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElementEditing
import dev.reactant.reactant.utils.content.item.itemStackOf
import org.bukkit.event.inventory.InventoryType.*
import org.bukkit.inventory.ItemStack

@UIElementName("inventory")
class ViewInventoryContainerElement(private val reactantUIView: ReactantUIView, allocatedSchedulerService: SchedulerService)
    : ReactantUIContainerElement(allocatedSchedulerService) {
    init {
        width = when (view.inventory.type) {
            CHEST -> actual(9)
            DISPENSER, DROPPER, CRAFTING -> actual(3)
            else -> throw UnsupportedOperationException("Unknown inventory type: ${view.inventory.type}")
        }

        height = actual(reactantUIView.inventory.size / (width as PositioningIntValue).value)
    }

    override fun edit() = object : ReactantUIContainerElementEditing<ViewInventoryContainerElement>(this) {}

    override val view: UIView get() = reactantUIView

    override var parent: UIElement?
        get() = null
        set(value) = throw UnsupportedOperationException("View element cannot have parent")

    override fun getBackgroundItemStack(x: Int, y: Int): ItemStack = itemStackOf()
}

