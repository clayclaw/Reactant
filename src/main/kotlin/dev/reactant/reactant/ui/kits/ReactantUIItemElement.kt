package dev.reactant.reactant.ui.kits

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.utils.content.item.itemStackOf
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@UIElementName("item")
open class ReactantUIItemElement(allocatedSchedulerService: SchedulerService)
    : ReactantUISingleSlotDisplayElement(allocatedSchedulerService) {

    public override var slotItem: ItemStack
        get() = super.slotItem
        set(value) = run { super.slotItem = value }

    @Deprecated("Confusing name, it may not only display", ReplaceWith("slotItem"))
    var displayItem: ItemStack
        get() = slotItem
        set(value) = run { slotItem = value }

    override fun edit(): ReactantUIItemElementEditing<ReactantUIItemElement> = ReactantUIItemElementEditing(this)
}

open class ReactantUIItemElementEditing<out T : ReactantUIItemElement>(element: T)
    : ReactantUISpanElementEditing<T>(element) {
    var slotItem: ItemStack by MutablePropertyDelegate(this.element::slotItem)

    @Deprecated("Confusing name, it may not only display", ReplaceWith("slotItem"))
    var displayItem: ItemStack
        get() = slotItem
        set(value) = run { slotItem = value }
}


fun ReactantUIElementEditing<ReactantUIElement>.item(
        displayMaterial: Material, creation: ReactantUIItemElementEditing<ReactantUIItemElement>.() -> Unit = {}) =
        item(itemStackOf(displayMaterial), creation)

fun ReactantUIElementEditing<ReactantUIElement>.item(
        displayItem: ItemStack = itemStackOf(),
        creation: ReactantUIItemElementEditing<ReactantUIItemElement>.() -> Unit = {}) {
    element.children.add(ReactantUIItemElement(element.allocatedSchedulerService).also {
        it.edit().also { creation -> creation.slotItem = displayItem }
                .apply(creation)
    })
}
