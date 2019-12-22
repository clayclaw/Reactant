package dev.reactant.reactant.ui.kits.container

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.style.fixed
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * The abstract class which can sort and render the children elements
 */
abstract class ReactantUIContainerElement(allocatedSchedulerService: SchedulerService, elementIdentifier: String)
    : ReactantUIElement(allocatedSchedulerService, elementIdentifier) {
    var overflowHidden = true

    open fun getBackgroundItemStack(x: Int, y: Int): ItemStack? = ItemStack(Material.AIR)

    abstract override fun edit(): ReactantUIContainerElementEditing<ReactantUIContainerElement>

    override fun render(relativePosition: Pair<Int, Int>): ItemStack? =
            getBackgroundItemStack(relativePosition.first, relativePosition.second)

    override fun renderVisibleElementsPositions(): LinkedHashMap<out ReactantUIElement, HashSet<Pair<Int, Int>>> {
        return super.renderVisibleElementsPositions().let {
            if (overflowHidden) LinkedHashMap(it.map { (el: ReactantUIElement, positions) ->
                el to positions.filter { position -> el == this || (el.position == fixed) || paddingExcludedBoundingClientRect.contains(position) }.toHashSet()
            }.toMap())
            else it
        }
    }
}

abstract class ReactantUIContainerElementEditing<out T : ReactantUIContainerElement>(element: T)
    : ReactantUIElementEditing<T>(element)
