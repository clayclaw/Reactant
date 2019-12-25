package dev.reactant.reactant.ui.kits

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.actual
import dev.reactant.reactant.utils.content.item.itemStackOf
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@UIElementName("item")
open class ReactantUIItemElement(allocatedSchedulerService: SchedulerService) : ReactantUISpanElement(allocatedSchedulerService, "item") {
    init {
        width = actual(1)
        height = actual(1)
    }

    var displayItem: ItemStack? = ItemStack(Material.AIR)
        set(value) = run { field = value }.also { view?.render() }

    override fun edit(): ReactantUIItemElementEditing<ReactantUIItemElement> = ReactantUIItemElementEditing(this, allocatedSchedulerService)


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
