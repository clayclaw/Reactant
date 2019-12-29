package dev.reactant.reactant.ui.kits

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.element.style.actual
import dev.reactant.reactant.utils.content.item.itemStackOf
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

abstract class ReactantUISingleSlotDisplayElement(allocatedSchedulerService: SchedulerService)
    : ReactantUISpanElement(allocatedSchedulerService) {
    init {
        width = actual(1)
        height = actual(1)
    }

    protected open var slotItem: ItemStack = itemStackOf(Material.AIR)
        set(value) = run { field = value }.also { view?.render() }

    override fun render(relativePosition: Pair<Int, Int>): ItemStack? = slotItem
}


abstract class ReactantUISingleSlotDisplayElementEditing<out T : ReactantUISingleSlotDisplayElement>(element: T)
    : ReactantUISpanElementEditing<T>(element)



