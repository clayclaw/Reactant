package io.reactant.reactant.ui.kits

import io.reactant.reactant.ui.editing.ReactantUIElementEditing
import io.reactant.reactant.ui.element.ReactantUIElement
import io.reactant.reactant.ui.element.UIElement
import io.reactant.reactant.ui.rendering.ElementSlot
import io.reactant.reactant.ui.rendering.RenderedItems
import io.reactant.reactant.utils.content.item.createItemStack
import io.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ReactantUIItemElement : ReactantUIElement("item") {
    var displayItem: ItemStack = ItemStack(Material.AIR)

    override fun render(parentFreeSpaceWidth: Int, parentFreeSpaceHeight: Int): RenderedItems =
            RenderedItems(hashMapOf((0 to 0) to ElementSlot(this, displayItem))).addMargin(this, parentFreeSpaceWidth, parentFreeSpaceHeight)

    override val width: Int = 1
    override val height: Int = 1
}

open class ReactantUIItemElementEditing(element: ReactantUIItemElement)
    : ReactantUIElementEditing<ReactantUIItemElement>(element) {
    var displayItem: ItemStack by MutablePropertyDelegate(element::displayItem)
}

fun ReactantUIElementEditing<out UIElement>.item(displayItem: ItemStack = createItemStack(),
                                                 creation: ReactantUIItemElementEditing.() -> Unit = {}) {
    element.children.add(ReactantUIItemElement()
            .also {
                ReactantUIItemElementEditing(it).also { creation ->
                    creation.displayItem = displayItem
                }.apply(creation)
            })
}
