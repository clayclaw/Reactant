package net.swamphut.swampium.ui.kits

import net.swamphut.swampium.ui.creation.SwUIElementCreation
import net.swamphut.swampium.ui.element.SwUIElement
import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.rendering.ElementSlot
import net.swamphut.swampium.ui.rendering.RenderedItems
import net.swamphut.swampium.utils.content.item.createItemStack
import net.swamphut.swampium.utils.delegation.MutablePropertyDelegate
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SwUIItemElement : SwUIElement("item") {
    var displayItem: ItemStack = ItemStack(Material.AIR)

    override fun render(parentFreeSpaceWidth: Int, parentFreeSpaceHeight: Int): RenderedItems =
            RenderedItems(hashMapOf((0 to 0) to ElementSlot(this, displayItem))).addMargin(this, parentFreeSpaceWidth, parentFreeSpaceHeight)

    override val width: Int = 1
    override val height: Int = 1
}

open class SwUIItemElementCreation(element: SwUIItemElement)
    : SwUIElementCreation<SwUIItemElement>(element) {
    var displayItem: ItemStack by MutablePropertyDelegate(element::displayItem)
}

fun SwUIElementCreation<out UIElement>.item(displayItem: ItemStack = createItemStack(),
                                            creation: SwUIItemElementCreation.() -> Unit = {}) {
    element.children.add(SwUIItemElement()
            .also {
                SwUIItemElementCreation(it).also { creation ->
                    creation.displayItem = displayItem
                }.apply(creation)
            })
}
