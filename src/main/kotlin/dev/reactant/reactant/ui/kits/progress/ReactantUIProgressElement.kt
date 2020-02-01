package dev.reactant.reactant.ui.kits.progress

import dev.reactant.reactant.extensions.trySetColor
import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.ui.element.style.actual
import dev.reactant.reactant.ui.kits.progress.ReactantUIProgressDirection.*
import dev.reactant.reactant.utils.delegation.MutablePropertyDelegate
import org.bukkit.Color
import org.bukkit.inventory.ItemStack

@UIElementName("progress")
open class ReactantUIProgressElement(allocatedSchedulerService: SchedulerService)
    : ReactantUIValueMappingItemElement<Double>(allocatedSchedulerService, 0.0) {
    init {
        height = actual(1)
    }


    private fun calculateMappingValue() {
        this.mappingValue = (value - min) / (max - min)
    }

    var min: Double = 0.0
        set(value) = run { field = value;calculateMappingValue() }
    var max: Double = 1.0
        set(value) = run { field = value;calculateMappingValue() }
    var value: Double = 0.0
        set(value) = run { field = value;calculateMappingValue() }

    fun updateValueMappingPattern() {
        valueMappingPattern = { value, (x, y) ->
            val barItemLength = when (direction) {
                LEFT_TO_RIGHT, RIGHT_TO_LEFT -> computedStyle?.offsetWidth ?: 0
                TOP_TO_BOTTOM, BOTTOM_TO_TOP -> computedStyle?.offsetHeight ?: 0
            }
            val barItemIndex = when (direction) {
                LEFT_TO_RIGHT -> x
                RIGHT_TO_LEFT -> computedStyle?.offsetWidth?.let { it - 1 - x } ?: 0
                TOP_TO_BOTTOM -> y
                BOTTOM_TO_TOP -> computedStyle?.offsetWidth?.let { it - 1 - x } ?: 0
            }
            theme(ProgressBarRenderInfo(barItemLength, barItemIndex, value))?.apply {
                if (color != null) trySetColor(color!!)
            }
        }
    }


    /**
     * Decide what PRogressBarRenderInfo will be send
     * For example, if a RIGHT_TO_LEFT progress is being rendering,
     *      the right most cell will be consider as first cell of the progress bar
     */
    var direction: ReactantUIProgressDirection = LEFT_TO_RIGHT
        set(value) = run { field = value;updateValueMappingPattern() }

    var theme: ProgressBarTheme = { _ -> null }
        set(value) = run { field = value;updateValueMappingPattern() }

    var color: Color? = null
        set(value) = run { field = value;updateValueMappingPattern() }

    override fun edit() = ReactantUIProgressElementEditing(this)
}

data class ProgressBarRenderInfo(val barItemLength: Int, val barItemIndex: Int, val value: Double)
typealias ProgressBarTheme = (ProgressBarRenderInfo) -> ItemStack?

enum class ReactantUIProgressDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP
}

class ReactantUIProgressElementEditing<out T : ReactantUIProgressElement>(element: T)
    : ReactantUIValueMappingItemElementEditing<T>(element) {
    var min: Double by MutablePropertyDelegate(element::min)
    var max: Double by MutablePropertyDelegate(element::max)
    var value: Double by MutablePropertyDelegate(element::value)
    var theme: ProgressBarTheme by MutablePropertyDelegate(element::theme)
    var color: Color? by MutablePropertyDelegate(element::color)
}

fun ReactantUIElementEditing<ReactantUIElement>.progress(theme: ProgressBarTheme, creation: ReactantUIProgressElementEditing<ReactantUIProgressElement>.() -> Unit) {
    element.children.add(ReactantUIProgressElement(element.allocatedSchedulerService)
            .also { it.edit().apply { this.theme = theme }.apply(creation) })
}
