package dev.reactant.reactant.ui.kits

import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.rendering.ElementSlot
import dev.reactant.reactant.ui.rendering.RenderedItems
import dev.reactant.reactant.utils.content.item.createItemStack
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

open class ReactantUIItemElement : ReactantUIElement("item") {
    override fun edit() = ReactantUIElementEditing(this)
    var displayItem: ItemStack = ItemStack(Material.AIR)

    override fun render(parentFreeSpaceWidth: Int, parentFreeSpaceHeight: Int): RenderedItems =
            RenderedItems(hashMapOf((0 to 0) to ElementSlot(this, displayItem))).addMargin(this, parentFreeSpaceWidth, parentFreeSpaceHeight)

    override val width: Int = 1
    override val height: Int = 1
}

open class ReactantUIItemElementEditing<T : ReactantUIItemElement>(element: T)
    : ReactantUIElementEditing<ReactantUIItemElement>(element) {
    var displayItem: ItemStack by MutablePropertyDelegate(element::displayItem)
}

fun ReactantUIElementEditing<out UIElement>.item(displayItem: ItemStack = createItemStack(),
                                                 creation: ReactantUIItemElementEditing<in ReactantUIItemElement>.() -> Unit = {}) {
    element.children.add(ReactantUIItemElement()
            .also {
                ReactantUIItemElementEditing(it).also { creation ->
                    creation.displayItem = displayItem
                }.apply(creation)
            })
}
