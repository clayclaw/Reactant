package dev.reactant.reactant.ui.element.style

interface PositioningStylePropertyValue {
}

data class PositioningIntValue(val value: Int) : PositioningStylePropertyValue {
    override fun toString(): String = "$value cell"
}

data class PositioningPercentageValue(val value: Float) : PositioningStylePropertyValue {
    override fun toString(): String = "$value%"
}

class PositioningAutoValue : PositioningStylePropertyValue {
    override fun toString(): String = "auto"
}

class PositioningFitContent : PositioningStylePropertyValue {
    override fun toString(): String = "fit-content"
}
