package dev.reactant.reactant.ui.element.style

import dev.reactant.reactant.ui.element.ReactantUIElement

abstract class ReactantUIElementStyle : UIElementStyleEditing {
    protected lateinit var el: ReactantUIElement

    var computedStyle: ReactantComputedStyle? = null

    final override var width: PositioningStylePropertyValue = actual(1)
        set(value) = run { field = value }.also { el.view?.render() }
    final override var height: PositioningStylePropertyValue = actual(1)
        set(value) = run { field = value }.also { el.view?.render() }

    final override var maxWidth: Int = Int.MAX_VALUE
        set(value) = run { field = value }.also { el.view?.render() }
    final override var maxHeight: Int = Int.MAX_VALUE
        set(value) = run { field = value }.also { el.view?.render() }
    final override var minWidth: Int = 0
        set(value) = run { field = value }.also { el.view?.render() }
    final override var minHeight: Int = 0
        set(value) = run { field = value }.also { el.view?.render() }

    final override var position: ElementPosition = static
        set(value) = run { field = value }.also { el.view?.render() }

    final override var marginTop: PositioningStylePropertyValue = actual(0)
        set(value) = run { field = value }.also { el.view?.render() }
    final override var marginRight: PositioningStylePropertyValue = actual(0)
        set(value) = run { field = value }.also { el.view?.render() }
    final override var marginBottom: PositioningStylePropertyValue = actual(0)
        set(value) = run { field = value }.also { el.view?.render() }
    final override var marginLeft: PositioningStylePropertyValue = actual(0)
        set(value) = run { field = value }.also { el.view?.render() }

    final override var paddingTop: PositioningStylePropertyValue = actual(0)
        set(value) = run { field = value }.also { el.view?.render() }
    final override var paddingRight: PositioningStylePropertyValue = actual(0)
        set(value) = run { field = value }.also { el.view?.render() }
    final override var paddingBottom: PositioningStylePropertyValue = actual(0)
        set(value) = run { field = value }.also { el.view?.render() }
    final override var paddingLeft: PositioningStylePropertyValue = actual(0)
        set(value) = run { field = value }.also { el.view?.render() }

    final override var top: PositioningStylePropertyValue = auto
        set(value) = run { field = value }.also { el.view?.render() }
    final override var right: PositioningStylePropertyValue = auto
        set(value) = run { field = value }.also { el.view?.render() }
    final override var bottom: PositioningStylePropertyValue = auto
        set(value) = run { field = value }.also { el.view?.render() }
    final override var left: PositioningStylePropertyValue = auto
        set(value) = run { field = value }.also { el.view?.render() }

    final override var zIndex: PositioningStylePropertyValue = auto
        set(value) = run { field = value }.also { el.view?.render() }

    override val computedZIndex: Int
        get() = (zIndex as? PositioningIntValue)?.value ?: el.parent?.computedZIndex ?: 0

    final override val offsetWidth: Int get() = computedStyle!!.offsetWidth
    final override val offsetHeight: Int get() = computedStyle!!.offsetHeight
    final override val boundingClientRect: BoundingRect get() = computedStyle!!.boundingClientRect
    final override val paddingExcludedBoundingClientRect: BoundingRect get() = computedStyle!!.paddingExcludedBoundingClientRect

    final override var display: ElementDisplay = block
        set(value) = run { field = value }.also { el.view?.render() }

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
        computedStyle!!.computeOffsetSize((width as PositioningIntValue).value, (height as PositioningIntValue).value)
        computedStyle!!.computeBoundingClientRect()
    }
}
