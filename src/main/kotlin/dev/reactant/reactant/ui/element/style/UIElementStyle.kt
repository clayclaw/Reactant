package dev.reactant.reactant.ui.element.style

interface UIElementStyle {
    // Modifiable Values
    val width: PositioningStylePropertyValue
    val height: PositioningStylePropertyValue

    val maxWidth: Int
    val maxHeight: Int

    val minWidth: Int
    val minHeight: Int

    val position: ElementPosition


    val marginTop: PositioningStylePropertyValue
    val marginRight: PositioningStylePropertyValue
    val marginBottom: PositioningStylePropertyValue
    val marginLeft: PositioningStylePropertyValue
    val margin: List<PositioningStylePropertyValue>

    val paddingTop: PositioningStylePropertyValue
    val paddingRight: PositioningStylePropertyValue
    val paddingBottom: PositioningStylePropertyValue
    val paddingLeft: PositioningStylePropertyValue
    val padding: List<PositioningStylePropertyValue>

    val top: PositioningStylePropertyValue
    val right: PositioningStylePropertyValue
    val bottom: PositioningStylePropertyValue
    val left: PositioningStylePropertyValue

    val zIndex: PositioningStylePropertyValue

    val computedZIndex: Int


    // Computed Values
    val offsetWidth: Int
    val offsetHeight: Int

    val boundingClientRect: BoundingRect

    val paddingExcludedBoundingClientRect: BoundingRect

    var display: ElementDisplay


    fun computeStyle()


    companion object {
        fun actual(value: Int) = PositioningStylePropertyValue.IntValue(value)
        fun percentage(value: Float) = PositioningStylePropertyValue.PercentageValue(value)
        val auto = object : PositioningStylePropertyValue.AutoValue {
            override fun toString(): String = "auto"
        }
        val fitContent = object : PositioningStylePropertyValue.FitContent {
            override fun toString(): String = "fit-content"
        }
        val fillParent = PositioningStylePropertyValue.PercentageValue(100F)

        val fixed = ElementPosition("fixed")
        val static = ElementPosition("static")
        val absolute = ElementPosition("absolute")
        val relative = ElementPosition("relative")

        val block = ElementDisplay("block")
        val inline = ElementDisplay("inline")
    }
}

/**
 *
 */
class ElementPosition(val name: String) {
    override fun toString(): String = name
}

class ElementDisplay(val name: String) {
    override fun toString(): String = name
}

data class BoundingRect(
        val top: Int,
        val right: Int,
        val bottom: Int,
        val left: Int
) {
    fun toPositions(): Set<Pair<Int, Int>> = (if (top < bottom) (top until bottom) else IntRange.EMPTY).flatMap { row ->
        (if (left < right) (left until right) else IntRange.EMPTY).map { col -> col to row /* x, y */ }
    }.toSet()

    fun contains(position: Pair<Int, Int>): Boolean =
            position.first in left until right && position.second in top until bottom
}

interface PositioningStylePropertyValue {
    data class IntValue(val value: Int) : PositioningStylePropertyValue {
        override fun toString(): String = "$value cell"
    }

    data class PercentageValue(val value: Float) : PositioningStylePropertyValue {
        override fun toString(): String = "$value%"
    }

    interface AutoValue : PositioningStylePropertyValue

    interface FitContent : PositioningStylePropertyValue
}

