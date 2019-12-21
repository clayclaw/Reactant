package dev.reactant.reactant.ui.element.style

import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.actual

interface UIElementStyleEditing : UIElementStyle {
    override var width: PositioningStylePropertyValue
    override var height: PositioningStylePropertyValue

    /**
     * Shorthand for setting width and height
     */
    fun size(width: PositioningStylePropertyValue, height: PositioningStylePropertyValue) {
        this.width = width
        this.height = height
    }

    /**
     * Shorthand for setting width and height with actual value
     */
    fun size(width: Int, height: Int) = size(actual(width), actual(height))

    override var maxWidth: Int
    override var maxHeight: Int

    /**
     * Shorthand for setting maxWidth and maxHeight
     */
    fun maxSize(maxWidth: Int, maxHeight: Int) {
        this.maxWidth = maxWidth
        this.maxHeight = maxHeight
    }

    override var minWidth: Int
    override var minHeight: Int

    /**
     * Shorthand for setting minWidth and minHeight
     */
    fun minSize(minWidth: Int, minHeight: Int) {
        this.minWidth = minWidth
        this.minHeight = minHeight
    }

    /**
     * Element positioning method
     * Available options: static, fixed, relative, absolute
     */
    override var position: ElementPosition

    override var marginTop: PositioningStylePropertyValue
    override var marginRight: PositioningStylePropertyValue
    override var marginBottom: PositioningStylePropertyValue
    override var marginLeft: PositioningStylePropertyValue

    override var margin
        get() = listOf(marginTop, marginRight, marginBottom, marginLeft)
        set(value) = expandDirectionalAttributes(value, actual(0))
                .let { marginTop = it[0]; marginRight = it[1]; marginBottom = it[2]; marginLeft = it[3] }

    /**
     * Shorthand for setting all margin
     */
    fun margin(vararg margin: PositioningStylePropertyValue) {
        this.margin = margin.toList()
    }

    /**
     * Shorthand for setting all margin with actual value
     */
    fun margin(vararg margin: Int) = margin(*margin.map { actual(it) }.toTypedArray())


    override var paddingTop: PositioningStylePropertyValue
    override var paddingRight: PositioningStylePropertyValue
    override var paddingBottom: PositioningStylePropertyValue
    override var paddingLeft: PositioningStylePropertyValue
    override var padding
        get() = listOf(paddingTop, paddingRight, paddingBottom, paddingLeft)
        set(value) = expandDirectionalAttributes(value, actual(0))
                .let { paddingTop = it[0]; paddingRight = it[1]; paddingBottom = it[2]; paddingLeft = it[3] }

    /**
     * Shorthand for setting all padding
     */
    fun padding(vararg padding: PositioningStylePropertyValue) {
        this.padding = padding.toList()
    }

    /**
     * Shorthand for setting all padding with actual value
     */
    fun padding(vararg padding: Int) = padding(*padding.map { actual(it) }.toTypedArray())

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
