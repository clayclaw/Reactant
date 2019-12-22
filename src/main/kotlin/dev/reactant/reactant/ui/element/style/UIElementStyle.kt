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

}

