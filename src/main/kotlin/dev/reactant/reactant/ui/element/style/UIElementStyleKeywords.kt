package dev.reactant.reactant.ui.element.style


fun actual(value: Int) = PositioningIntValue(value)

fun percentage(value: Float) = PositioningPercentageValue(value)

val auto = PositioningAutoValue()
val fitContent = PositioningFitContent()
val fillParent = PositioningPercentageValue(100F)

val fixed = ElementPosition("fixed")
val static = ElementPosition("static")
val absolute = ElementPosition("absolute")
val relative = ElementPosition("relative")

val block = ElementDisplay("block")
val inline = ElementDisplay("inline")

val interactInherit = ElementInteractEventsReceivingProperty("inherit")
val interactNone = ElementInteractEventsReceivingProperty("none")
val interactAll = ElementInteractEventsReceivingProperty("all")

class ElementInteractEventsReceivingProperty(val name: String) {
    override fun toString(): String = name
}

class ElementPosition(val name: String) {
    override fun toString(): String = name
}

class ElementDisplay(val name: String) {
    override fun toString(): String = name
}

