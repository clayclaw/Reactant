package dev.reactant.reactant.ui.kits

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.PositioningStylePropertyValue
import dev.reactant.reactant.ui.element.style.actual
import dev.reactant.reactant.utils.content.item.itemStackOf
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@UIElementName("item")
open class ReactantUIItemElement(allocatedSchedulerService: SchedulerService) : ReactantUISpanElement(allocatedSchedulerService, "item") {
    var displayItem: ItemStack? = ItemStack(Material.AIR)

    override fun edit(): ReactantUIItemElementEditing<ReactantUIItemElement> = ReactantUIItemElementEditing(this, allocatedSchedulerService)

    override var width: PositioningStylePropertyValue = actual(1)
        set(value) = throw UnsupportedOperationException("item element cannot have width")
    override var height: PositioningStylePropertyValue = actual(1)
        set(value) = throw UnsupportedOperationException("item element cannot have height")

    override fun render(relativePosition: Pair<Int, Int>): ItemStack? {
        return displayItem
    }
}

open class ReactantUIItemElementEditing<out T : ReactantUIItemElement>(element: T, allocatedSchedulerService: SchedulerService)
    : ReactantUISpanElementEditing<T>(element, allocatedSchedulerService) {
    var displayItem: ItemStack? by MutablePropertyDelegate(this.element::displayItem)
}


fun ReactantUIElementEditing<ReactantUIElement>.item(displayMaterial: Material,
                                                     creation: ReactantUIItemElementEditing<ReactantUIItemElement>.() -> Unit = {}) =
        item(itemStackOf(displayMaterial), creation)

fun ReactantUIElementEditing<ReactantUIElement>.item(displayItem: ItemStack = itemStackOf(),
                                                     creation: ReactantUIItemElementEditing<ReactantUIItemElement>.() -> Unit = {}) {
    element.children.add(ReactantUIItemElement(element.allocatedSchedulerService).also {
        it.edit().also { creation -> creation.displayItem = displayItem }
                .apply(creation)
    })
}
