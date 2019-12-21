package dev.reactant.reactant.ui.element.style

import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.actual
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.auto
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.block
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.static

abstract class ReactantUIElementStyle : UIElementStyleEditing {
    lateinit protected var el: ReactantUIElement

    var computedStyle: ReactantComputedStyle? = null

    override var width: PositioningStylePropertyValue = actual(1)
    override var height: PositioningStylePropertyValue = actual(1)

    override var maxWidth: Int = Int.MAX_VALUE
    override var maxHeight: Int = Int.MAX_VALUE
    override var minWidth: Int = 0
    override var minHeight: Int = 0

    override var position: ElementPosition = static

    override var marginTop: PositioningStylePropertyValue = actual(0)
    override var marginRight: PositioningStylePropertyValue = actual(0)
    override var marginBottom: PositioningStylePropertyValue = actual(0)
    override var marginLeft: PositioningStylePropertyValue = actual(0)

    override var paddingTop: PositioningStylePropertyValue = actual(0)
    override var paddingRight: PositioningStylePropertyValue = actual(0)
    override var paddingBottom: PositioningStylePropertyValue = actual(0)
    override var paddingLeft: PositioningStylePropertyValue = actual(0)

    override var top: PositioningStylePropertyValue = auto
    override var right: PositioningStylePropertyValue = auto
    override var bottom: PositioningStylePropertyValue = auto
    override var left: PositioningStylePropertyValue = auto

    override var zIndex: PositioningStylePropertyValue = auto

    override val computedZIndex: Int
        get() = (zIndex as? PositioningStylePropertyValue.IntValue)?.value ?: el.parent?.computedZIndex ?: 0

    override val offsetWidth: Int get() = computedStyle!!.offsetWidth
    override val offsetHeight: Int get() = computedStyle!!.offsetHeight
    override val boundingClientRect: BoundingRect get() = computedStyle!!.boundingClientRect
    override val paddingExcludedBoundingClientRect: BoundingRect get() = computedStyle!!.paddingExcludedBoundingClientRect

    override var display: ElementDisplay = block

    protected fun clearComputedStyle() {
        this.computedStyle = null;
        el.children.map { it as ReactantUIElement }.forEach { it.clearComputedStyle() }
    }

    protected fun initializeComputedStyle() {
        if (computedStyle == null) computedStyle = ReactantComputedStyle(el)
        el.children.map { it as ReactantUIElement }.forEach { it.initializeComputedStyle() }
    }

    protected fun recursivelyCompute(reversed: Boolean = false, styleComputation: (ReactantComputedStyle) -> Unit) {
        if (!reversed) styleComputation(this.computedStyle!!)
        el.children.map { it as ReactantUIElement }.forEach { it.recursivelyCompute(reversed, styleComputation) }
        if (reversed) styleComputation(this.computedStyle!!)
    }

    override fun computeStyle() {
        if (el.rootElement != this) throw UnsupportedOperationException("Only root element can compute style")
        clearComputedStyle()
        initializeComputedStyle()
        computedStyle!!.computeOffsetSize((width as PositioningStylePropertyValue.IntValue).value, (height as PositioningStylePropertyValue.IntValue).value)
        computedStyle!!.computeBoundingClientRect()
    }
}
