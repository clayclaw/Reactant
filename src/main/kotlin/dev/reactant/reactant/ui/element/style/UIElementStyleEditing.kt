package dev.reactant.reactant.ui.element.style

import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.actual

interface UIElementStyleEditing : UIElementStyle {
    override var width: PositioningStylePropertyValue
    override var height: PositioningStylePropertyValue

    override var maxWidth: Int
    override var maxHeight: Int

    override var minWidth: Int
    override var minHeight: Int

    override var position: ElementPosition

    override var marginTop: PositioningStylePropertyValue
    override var marginRight: PositioningStylePropertyValue
    override var marginBottom: PositioningStylePropertyValue
    override var marginLeft: PositioningStylePropertyValue
    override var margin
        get() = listOf(marginTop, marginRight, marginBottom, marginLeft)
        set(value) = expandDirectionalAttributes(value, actual(0))
                .let { marginTop = it[0]; marginRight = it[1]; marginBottom = it[2]; marginLeft = it[3] }

    fun margin(vararg margin: PositioningStylePropertyValue) {
        this.margin = margin.toList()
    }


    override var paddingTop: PositioningStylePropertyValue
    override var paddingRight: PositioningStylePropertyValue
    override var paddingBottom: PositioningStylePropertyValue
    override var paddingLeft: PositioningStylePropertyValue
    override var padding
        get() = listOf(paddingTop, paddingRight, paddingBottom, paddingLeft)
        set(value) = expandDirectionalAttributes(value, actual(0))
                .let { paddingTop = it[0]; paddingRight = it[1]; paddingBottom = it[2]; paddingLeft = it[3] }

    fun padding(vararg padding: PositioningStylePropertyValue) {
        this.padding = padding.toList()
    }

    override var top: PositioningStylePropertyValue
    override var right: PositioningStylePropertyValue
    override var bottom: PositioningStylePropertyValue
    override var left: PositioningStylePropertyValue

    override var zIndex: PositioningStylePropertyValue

    override var display: ElementDisplay

    companion object {
        fun <T : Any> expandDirectionalAttributes(expanding: List<T>, defaultValue: T): List<T> {
            if (expanding.size > 4) throw IllegalArgumentException(
                    "Directional attributes cannot have more than 4 elements, but found $expanding")
            val expanded = arrayListOf(defaultValue, defaultValue, defaultValue, defaultValue)
            expanding.getOrNull(0)?.let { value -> (0..3).forEach { expanded[it] = value } }
            expanding.getOrNull(1)?.let { value -> setOf(1, 3).forEach { expanded[it] = value } }
            expanding.getOrNull(2)?.let { value -> expanded[2] = value }
            expanding.getOrNull(3)?.let { value -> expanded[3] = value }
            return expanded
        }
    }
}
