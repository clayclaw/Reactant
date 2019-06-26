package io.reactant.reactant.ui.kits

import io.reactant.reactant.ui.editing.ReactantUIElementEditing
import io.reactant.reactant.ui.element.ElementDisplay
import io.reactant.reactant.ui.element.UIElement
import io.reactant.reactant.ui.element.type.sizing.ResizableElement
import io.reactant.reactant.ui.element.type.sizing.ResizableElementsEditing
import io.reactant.reactant.ui.kits.container.ReactantUIContainerElement
import io.reactant.reactant.ui.kits.container.ReactantUIContainerElementEditing
import io.reactant.reactant.utils.content.item.createItemStack
import io.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.inventory.ItemStack

class ReactantUIDivElement : ReactantUIContainerElement("div"), ResizableElement {
    override var height: Int = UIElement.WRAP_CONTENT
    override var width: Int = UIElement.MATCH_PARENT
    override var display = ElementDisplay.BLOCK

    var fillPattern: (relativeX: Int, relativeY: Int) -> ItemStack = { _, _ -> createItemStack() }

    override fun getBackgroundItemStack(x: Int, y: Int): ItemStack = fillPattern(x, y)
}

open class ReactantUIDivElementEditing(element: ReactantUIDivElement)
    : ReactantUIContainerElementEditing<ReactantUIDivElement>(element), ResizableElementsEditing<ReactantUIDivElement> {
    var overflowHidden by MutablePropertyDelegate(element::overflowHidden)
    var fillPattern by MutablePropertyDelegate(element::fillPattern)
    fun fill(itemStack: ItemStack) {
        fillPattern = { _, _ -> itemStack.clone() }
    }
}

fun ReactantUIElementEditing<out UIElement>.div(creation: ReactantUIDivElementEditing.() -> Unit) {
    element.children.add(ReactantUIDivElement().also { ReactantUIDivElementEditing(it).apply(creation) })
}
