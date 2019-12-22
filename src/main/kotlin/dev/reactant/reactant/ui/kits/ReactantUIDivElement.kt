package dev.reactant.reactant.ui.kits

import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.*
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElement
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElementEditing
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.inventory.ItemStack

@UIElementName("div")
open class ReactantUIDivElement(elementIdentifier: String = "div") : ReactantUIContainerElement(elementIdentifier) {
    override fun edit() = ReactantUIDivElementEditing(this)

    override var width: PositioningStylePropertyValue = auto
    override var height: PositioningStylePropertyValue = fitContent

    override var display: ElementDisplay = block

    override var minHeight: Int = 1

    var fillPattern: (relativeX: Int, relativeY: Int) -> ItemStack? = { _, _ -> null }

    override fun getBackgroundItemStack(x: Int, y: Int): ItemStack? = fillPattern(x, y)
}

open class ReactantUIDivElementEditing<out T : ReactantUIDivElement>(element: T)
    : ReactantUIContainerElementEditing<T>(element) {
    var overflowHidden by MutablePropertyDelegate(this.element::overflowHidden)
    var fillPattern by MutablePropertyDelegate(this.element::fillPattern)
    fun fill(itemStack: ItemStack?) {
        fillPattern = { _, _ -> itemStack?.clone() }
    }
}

fun ReactantUIElementEditing<ReactantUIElement>.div(creation: ReactantUIDivElementEditing<ReactantUIDivElement>.() -> Unit) {
    element.children.add(ReactantUIDivElement().also { ReactantUIDivElementEditing(it).apply(creation) })
}
