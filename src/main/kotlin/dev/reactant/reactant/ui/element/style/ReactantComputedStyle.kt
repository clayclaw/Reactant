package dev.reactant.reactant.ui.element.style

import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.element.style.PositioningStylePropertyValue.*
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.absolute
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.actual
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.block
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.fitContent
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.fixed
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.relative
import dev.reactant.reactant.ui.element.style.UIElementStyle.Companion.static
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class ReactantComputedStyle(val el: ReactantUIElement) {

    var offsetWidth: Int by Delegates.notNull()
    var offsetHeight: Int by Delegates.notNull()

    var offsetComputed = false;


    val revisedWidth: PositioningStylePropertyValue
        get() = when (el.width) {
            is FitContent, is AutoValue -> fitContent
            // consider as percentage size as fit-content if its parent also is fit-content size
            is PercentageValue -> when ((el.parent as ReactantUIElement).computedStyle!!.revisedWidth) {
                is FitContent -> fitContent
                else -> el.width
            }
            else -> el.width
        }

    val revisedHeight: PositioningStylePropertyValue
        get() = when (el.height) {
            is FitContent, is AutoValue -> fitContent
            // consider as percentage size as fit-content if its parent also is fit-content size
            is PercentageValue -> when ((el.parent as ReactantUIElement).computedStyle!!.revisedHeight) {
                is FitContent -> fitContent
                else -> el.height
            }
            else -> el.height
        }

    /**
     * Position the based on parent
     */
    private var relativePositionLeft = 0
    private var relativePositionTop = 0


    var paddingTop: Int by Delegates.notNull()
    var paddingRight: Int by Delegates.notNull()
    var paddingBottom: Int by Delegates.notNull()
    var paddingLeft: Int by Delegates.notNull()


    var marginTop: Int by Delegates.notNull()
    var marginRight: Int by Delegates.notNull()
    var marginBottom: Int by Delegates.notNull()
    var marginLeft: Int by Delegates.notNull()


    var top: Int by Delegates.notNull()
    var right: Int by Delegates.notNull()
    var bottom: Int by Delegates.notNull()
    var left: Int by Delegates.notNull()

    fun computeOffsetSize() {
        if (offsetComputed) return;
        offsetComputed = true;
        paddingTop = calculateWidthBasedValue(el.paddingTop)
        paddingRight = calculateWidthBasedValue(el.paddingRight)
        paddingBottom = calculateWidthBasedValue(el.paddingBottom)
        paddingLeft = calculateWidthBasedValue(el.paddingLeft)

        marginTop = calculateWidthBasedValue(el.marginTop)
        marginRight = calculateWidthBasedValue(el.marginRight)
        marginBottom = calculateWidthBasedValue(el.marginBottom)
        marginLeft = calculateWidthBasedValue(el.marginLeft)

        top = calculateHeightBasedValue(el.top)
        right = calculateWidthBasedValue(el.right)
        bottom = calculateHeightBasedValue(el.top)
        left = calculateWidthBasedValue(el.top)



        if (revisedWidth !is FitContent) {
            offsetWidth = getOffsetSize(true)
        }

        if (revisedHeight !is FitContent) {
            offsetHeight = getOffsetSize(false)
        }

        el.children.map { it as ReactantUIElement }.forEach { it.computedStyle!!.computeOffsetSize() }
        computeChildrenPosition()


    }

    fun computeBoundingClientRect() {
        boundingClientRect = when (el.position) {
            fixed -> calculatePositionByTopRightBottomLeft(el.rootElement)
            static -> BoundingRect(calculatedPositionTop, calculatedPositionLeft + offsetWidth, calculatedPositionTop + offsetHeight, calculatedPositionLeft)
            relative -> calculatePositionByTopRightBottomLeft(el.parent).run {
                BoundingRect(top + calculatedPositionTop, right + calculatedPositionLeft, bottom + calculatedPositionTop, left + calculatedPositionLeft)
            }
            absolute -> calculatePositionByTopRightBottomLeft(el.parent).run {
                BoundingRect(
                        top + (el.parent?.boundingClientRect?.top ?: 0),
                        right + (el.parent?.boundingClientRect?.left ?: 0),
                        bottom + (el.parent?.boundingClientRect?.top ?: 0),
                        left + (el.parent?.boundingClientRect?.left ?: 0)
                )
            }
            else -> throw IllegalStateException("Unknown position value")
        }

        val paddingExcludedBoundingClientRectTop = (boundingClientRect.top + paddingTop).coerceAtMost(boundingClientRect.bottom)
        val paddingExcludedBoundingClientRectRight = (boundingClientRect.right + paddingRight).coerceAtLeast(boundingClientRect.left)
        paddingExcludedBoundingClientRect = BoundingRect(
                paddingExcludedBoundingClientRectTop,
                paddingExcludedBoundingClientRectRight,
                (boundingClientRect.bottom - paddingBottom).coerceAtLeast(paddingExcludedBoundingClientRectTop),
                (boundingClientRect.left - paddingLeft).coerceAtMost(paddingExcludedBoundingClientRectRight)
        )

        el.children.map { it as ReactantUIElement }.forEach { it.computedStyle!!.computeBoundingClientRect() }
    }

    private fun calculateWidthBasedValue(value: PositioningStylePropertyValue): Int = when (value) {
        is IntValue -> value.value
        is PercentageValue -> ((value.value / 100) * (el.parent?.offsetWidth ?: 0)).roundToInt()
        else -> 0
    }

    private fun calculateHeightBasedValue(value: PositioningStylePropertyValue): Int = when (value) {
        is IntValue -> value.value
        is PercentageValue -> ((value.value / 100) * (el.parent?.offsetHeight ?: 0)).roundToInt()
        else -> 0
    }


    class ChildrenRow(val widthPerRow: Int) {
        val children: ArrayList<ReactantUIElement> = arrayListOf()
        val width
            get() = children.map { it.computedStyle!!.offsetWidth + it.computedStyle!!.marginLeft + it.computedStyle!!.marginRight }
                    .sum() ?: 0
        val totalAutoMargin
            get() = children.map {
                ((it.marginLeft as? AutoValue)?.let { 1 } ?: 0) + ((it.marginLeft as? AutoValue)?.let { 1 } ?: 0)
            }.sum()

        val autoMarginSpace get() = (max(0, widthPerRow - width) / totalAutoMargin)

        val rowHeight
            get() = children.map { it.computedStyle!!.offsetHeight + it.computedStyle!!.marginTop + it.computedStyle!!.marginBottom }.max()
                    ?: 0

        fun canInsert(el: ReactantUIElement): Boolean = width == 0 || width + el.offsetWidth <= widthPerRow
    }

    /**
     * Compute the children position, and set the height and width of "fit-content" parent
     */
    fun computeChildrenPosition() {
        val widthPerRow = if (el.width == fitContent) Int.MAX_VALUE else max(0, offsetWidth - paddingLeft - paddingRight)
        val rows = arrayListOf(ChildrenRow(widthPerRow))
        el.children.map { it as ReactantUIElement }.forEach {
            if (!rows.last().canInsert(it) || (it.display == block && rows.last().children.size > 0)) rows.add(ChildrenRow(widthPerRow))
            rows.last().children.add(it)
            if (it.display == block) rows.add(ChildrenRow(widthPerRow));
        }

        var allocatingTop = paddingTop
        var allocatingLeft = paddingLeft

        rows.forEach { row ->
            row.children.forEach {
                val leftMargin = ((it.marginLeft as? AutoValue)?.let { row.autoMarginSpace }
                        ?: it.computedStyle!!.marginLeft)
                val rightMargin = ((it.marginRight as? AutoValue)?.let { row.autoMarginSpace }
                        ?: it.computedStyle!!.marginRight)
                it.computedStyle!!.relativePositionTop = allocatingTop + it.computedStyle!!.marginTop
                it.computedStyle!!.relativePositionLeft = allocatingLeft + leftMargin
                allocatingLeft += it.offsetWidth + leftMargin + rightMargin
            }
            allocatingTop += row.rowHeight
            allocatingLeft = paddingLeft
        }

        // calculate the fit-content element offsetHeight and offsetWidth at this moment
        if (revisedHeight is FitContent) {
            offsetHeight = max(el.minHeight, min(el.maxHeight, rows.map { it.rowHeight }.sum() + paddingTop + paddingBottom))
        }
        if (revisedWidth is FitContent) {
            offsetWidth = rows.map { it.width }.max() ?: 0
        }
    }


    private fun getOffsetSize(isWidth: Boolean): Int {

        val sizeGetter: (UIElement) -> PositioningStylePropertyValue = if (isWidth) UIElement::width else UIElement::height
        val offsetSizeGetter: (UIElement) -> Int = if (isWidth) UIElement::offsetWidth else UIElement::offsetHeight  // todo: problem, this value is not computed

        val sizeValue = sizeGetter(el);

        val limiter: (UIElement, Int) -> Int =
                if (isWidth) { target: UIElement, size: Int -> max(target.minWidth, min(target.maxWidth, size)) }
                else { target: UIElement, size: Int -> max(target.minHeight, min(target.maxHeight, size)) }

        return limiter(el, when (sizeValue) {
            is IntValue -> return (sizeGetter(el) as IntValue).value
            // cannot compute auto fit height in this stage
            is FitContent, is AutoValue -> throw UnsupportedOperationException("fit-content")
            is PercentageValue -> (((el.parent?.let { offsetSizeGetter(it) })
                    ?: 0) * (sizeValue.value / 100)).roundToInt()
            else -> 0
        })
    }


    private fun calculatePositionByTopRightBottomLeft(scaleByEl: UIElement?): BoundingRect {
        /**
         * @param offsetBetweenEnd true if this position is describing the offset between the end of the parent/root and the edge of the element
         */
        fun calculatePosition(length: Int, rawPosition: PositioningStylePropertyValue, scaleBy: Int, oppositeValue: PositioningStylePropertyValue, offsetBetweenEnd: Boolean): Int {
            return when (rawPosition) {
                is IntValue -> rawPosition.value
                is PercentageValue -> (scaleBy * rawPosition.value / 100).roundToInt()
                is AutoValue -> if (oppositeValue !is AutoValue) {
                    val oppositeActual = calculatePosition(length, oppositeValue, scaleBy, rawPosition, !offsetBetweenEnd);
                    if (offsetBetweenEnd) oppositeActual + length
                    else oppositeActual - length
                } else 0
                else -> 0
            }.let { if (offsetBetweenEnd) scaleBy - it else it }
        }

        val revisedTop = calculatePosition(offsetHeight, el.top, scaleByEl?.offsetHeight ?: 0, el.bottom, false)
        val revisedBottom = calculatePosition(offsetHeight, el.bottom, scaleByEl?.offsetHeight
                ?: 0, actual(revisedTop), true)
        val revisedLeft = calculatePosition(offsetWidth, el.left, scaleByEl?.offsetWidth ?: 0, el.right, false)
        val revisedRight = calculatePosition(offsetWidth, el.right, scaleByEl?.offsetWidth
                ?: 0, actual(revisedLeft), true)

        return BoundingRect(revisedTop, revisedRight, revisedBottom, revisedLeft)
    }

    private val calculatedPositionTop get() = relativePositionTop + (el.parent?.boundingClientRect?.top ?: 0)
    private val calculatedPositionLeft get() = relativePositionLeft + (el.parent?.boundingClientRect?.left ?: 0)

    var boundingClientRect: BoundingRect by Delegates.notNull()
    var paddingExcludedBoundingClientRect: BoundingRect by Delegates.notNull()
}

