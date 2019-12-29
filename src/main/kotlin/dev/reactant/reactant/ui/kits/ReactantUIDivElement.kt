package dev.reactant.reactant.ui.kits

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.auto
import dev.reactant.reactant.ui.element.style.block
import dev.reactant.reactant.ui.element.style.fitContent
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElement
import dev.reactant.reactant.ui.kits.container.ReactantUIContainerElementEditing
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.inventory.ItemStack

@UIElementName("div")
open class ReactantUIDivElement(allocatedSchedulerService: SchedulerService)
    : ReactantUIContainerElement(allocatedSchedulerService) {
    init {
        width = auto
        height = fitContent
        display = block
        minHeight = 1
    }

    override fun edit() = ReactantUIDivElementEditing(this)

    open var fillPattern: (relativeX: Int, relativeY: Int) -> ItemStack? = { _, _ -> null }
        set(value) = run { field = value }.also { view?.render() }

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
    element.children.add(ReactantUIDivElement(element.allocatedSchedulerService).also { it.edit().apply(creation) })
}
