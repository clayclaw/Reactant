package dev.reactant.uikit.element.progress

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.kits.ReactantUIDivElement
import dev.reactant.reactant.ui.kits.ReactantUIDivElementEditing
import org.bukkit.inventory.ItemStack

/**
 * An element that use for display different item based on different value
 */
abstract class ReactantUIValueMappingItemElement<T>(allocatedSchedulerService: SchedulerService, defaultValue: T)
    : ReactantUIDivElement(allocatedSchedulerService) {

    protected var valueMappingPattern: ValueBasedFillPattern<T> = { _, _ -> null }
        set(value) = run { field = value;updateFillPattern() }

    private fun updateFillPattern() {
        this.fillPattern = { relativeX, relativeY -> valueMappingPattern(mappingValue, (relativeX to relativeY)) }
        view?.render()
    }

    protected var mappingValue: T = defaultValue
        set(value) = run { field = value;updateFillPattern() }

    abstract override fun edit(): ReactantUIValueMappingItemElementEditing<ReactantUIValueMappingItemElement<T>>
}

open class ReactantUIValueMappingItemElementEditing<out T : ReactantUIValueMappingItemElement<*>>(element: T)
    : ReactantUIDivElementEditing<T>(element)

typealias ValueBasedFillPattern<T> = (T, Pair<Int, Int>) -> ItemStack?
