package dev.reactant.reactant.ui.kits

import dev.reactant.reactant.ui.editing.UIElementEditing
import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.PositioningStylePropertyValue
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.actual
import dev.reactant.reactant.utils.content.item.itemStackOf
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@UIElementName("item")
open class ReactantUIItemElement : ReactantUISpanElement("item") {
    var displayItem: ItemStack = ItemStack(Material.AIR)

    override fun edit(): ReactantUIItemElementEditing<ReactantUIItemElement> = ReactantUIItemElementEditing(this)

    override var width: PositioningStylePropertyValue = actual(1)
        set(value) = throw UnsupportedOperationException("item element cannot have width")
    override var height: PositioningStylePropertyValue = actual(1)
        set(value) = throw UnsupportedOperationException("item element cannot have height")

    override fun render(relativePosition: Pair<Int, Int>): ItemStack? {
        return displayItem
    }
}

open class ReactantUIItemElementEditing<out T : ReactantUIItemElement>(element: T)
    : ReactantUISpanElementEditing<T>(element) {
    var displayItem: ItemStack by MutablePropertyDelegate(this.element::displayItem)
}


fun UIElementEditing<UIElement>.item(displayMaterial: Material = Material.AIR,
                                     creation: ReactantUIItemElementEditing<ReactantUIItemElement>.() -> Unit = {}) =
        item(itemStackOf(displayMaterial), creation)

fun UIElementEditing<UIElement>.item(displayItem: ItemStack = itemStackOf(),
                                     creation: ReactantUIItemElementEditing<ReactantUIItemElement>.() -> Unit = {}) {
    element.children.add(ReactantUIItemElement()
            .also {
                ReactantUIItemElementEditing(it).also { creation ->
                    creation.displayItem = displayItem
                }.apply(creation)
            })
}
