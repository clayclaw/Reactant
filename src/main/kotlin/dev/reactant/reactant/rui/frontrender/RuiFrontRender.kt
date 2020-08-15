package dev.reactant.reactant.rui.frontrender

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.rui.RuiRootUI
import dev.reactant.rui.dom.*
import dev.reactant.rui.render.ElementDOMTreeNode
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class VisualBox(
        var zIndex: Int = 0,
        // box two point are exclusive, e.g. (x1=-1, y1=-1, x2=1, y2=1) mean only included (x=0, y=0)
        val x1: Int = 0,
        val y1: Int = 0,
        val x2: Int = 0,
        val y2: Int = 0,
        var eventTarget: ElementDOMTreeNode?,
        var background: (BackgroundRenderingInfo) -> ItemStack?
) {

    // remind visibility

    // null if interactEvent is None


    val pixels: Sequence<Pair<Int, Int>> by lazy(LazyThreadSafetyMode.NONE) {
        ReactantCore.logger.info("${(x1 + 1)} until ${x2}, ${y1 + 1} until ${y2}")
        ((x1 + 1) until x2).asSequence().flatMap { x ->
            ((y1 + 1) until y2).asSequence().map { y -> x to y }
        }
    }

    val width by lazy(LazyThreadSafetyMode.NONE) { x2 - x1 - 2 }
    val height by lazy(LazyThreadSafetyMode.NONE) { y2 - y1 - 2 }
}

class InProgressConversion(
        val eventTarget: ElementDOMTreeNode,
        val attributes: RuiDivProps,
        val parent: InProgressConversion?
) {
    lateinit var children: List<InProgressConversion>

    val isParentSizeFactor = attributes.position == RuiPosition.Static || attributes.position == RuiPosition.Relative

    var marginWidth = attributes.marginLeft + attributes.marginRight
    var marginHeight = attributes.marginTop + attributes.marginBottom
    var paddingWidth = attributes.paddingLeft + attributes.paddingRight
    var paddingHeight = attributes.paddingTop + attributes.paddingBottom

    var computedCrossAxisItemWidth: Int = 0
    var computedCrossAxisItemHeight: Int = 0

    /**
     * If can allocate the value, the parent should allocate to it
     * but not a strict size, just a suggestion to parent how many it should get
     * If calculating cross axis, this value should be ignored
     */
    var computedMinAllocationWidth: Int = 0
    var computedMinAllocationHeight: Int = 0


    // == step 3

    lateinit var flexContainers: List<FlexContainer>

    var finalWidth: Int = 0
    var finalHeight: Int = 0


    var finalUIX: Int = 0
    var finalUIY: Int = 0
}

fun constructInProgressConversion(node: ElementDOMTreeNode, parent: InProgressConversion? = null): InProgressConversion? {
    (node.element.props as RuiDivProps).let { attribute ->
        if (attribute.display == RuiDisplay.None) return null
        val result = InProgressConversion(node, attribute, parent)
        ReactantCore.logger.info("${node.element.props.key} <")
        result.children = node.children.mapNotNull { constructInProgressConversion(it, result) }.toList()
        ReactantCore.logger.info(" > ${node.element.props.key} ")
        return result
    }
}

class FlexContainer(flexDirection: RuiFlexDirection, mainSize: Int, val reverse: Boolean) {
    val children: LinkedList<InProgressConversion> = LinkedList()
    val isRow = flexDirection == RuiFlexDirection.Row || flexDirection == RuiFlexDirection.RowReverse

    val actualCrossSize by lazy {
        children.asSequence().map {
            if (isRow) it.finalHeight
            else it.finalWidth
        }.maxOrNull() ?: 0 // can be width or height
    }

    var actualMainSize = 0
    var mainAxisRemain = mainSize
        private set

    fun addChildren(newChildren: InProgressConversion) {
        if (reverse) children.addFirst(newChildren)
        else children.addLast(newChildren)

        val itemMainSize =
                if (isRow) newChildren.computedMinAllocationWidth
                else newChildren.computedMinAllocationHeight
        mainAxisRemain -= itemMainSize
        actualMainSize += itemMainSize
    }

    fun shouldWarp(next: InProgressConversion): Boolean {
        ReactantCore.logger.info("WARP CONDITIONS: ${children.size == 0} ${isRow && next.attributes.width == RuiSizing.FillParent} ${isRow && next.attributes.height == RuiSizing.FillParent}")
        if (children.size == 0) return false
//        if (isRow && next.attributes.width == RuiSizing.fillParent) return true
//        else if (!isRow && next.attributes.height == RuiSizing.fillParent) return true

        val nextMainSize =
                if (isRow) next.computedMinAllocationWidth
                else next.computedMinAllocationHeight
        ReactantCore.logger.info("CHECKING: ${mainAxisRemain} < ${nextMainSize}")
        return (mainAxisRemain < nextMainSize)
    }
}

fun calculateSpecialPositioning(target: InProgressConversion, basedX: Int, basedY: Int, basedWidth: Int, basedHeight: Int) {
    target.finalUIX = when {
        target.attributes.left != RuiPositioning.Auto -> basedX + target.attributes.left
        target.attributes.right != RuiPositioning.Auto -> basedX + basedWidth - target.attributes.right - target.finalWidth
        else -> basedX
    }

    target.finalUIY = when {
        target.attributes.top != RuiPositioning.Auto -> basedY + target.attributes.top
        target.attributes.bottom != RuiPositioning.Auto -> basedY + basedHeight - target.attributes.bottom - target.finalHeight
        else -> basedY
    }
}

/**
 * Will ignore display=none
 */
fun convertTreeToVisualBoxes(conversion: InProgressConversion, uiWidth: Int, uiHeight: Int): List<VisualBox> {
    fun computeMaxMinSize(
            conversion: InProgressConversion
    ) {
        val isFlexRow = conversion.attributes.flexDirection == RuiFlexDirection.Row || conversion.attributes.flexDirection == RuiFlexDirection.RowReverse
        val sizeFactorChildren = conversion.children.onEach { computeMaxMinSize(it) }
                .filter { it.isParentSizeFactor }

        // = asking what width the parent should be, assume parent width is not limited
        conversion.computedCrossAxisItemWidth = if (isFlexRow) {
            when (conversion.attributes.width) {
                // = non limited width, = all item in single row is also ok
                RuiSizing.FillParent -> sizeFactorChildren.sumBy { it.computedMinAllocationWidth } + conversion.paddingWidth // actually is same
                RuiSizing.FitContent -> sizeFactorChildren.sumBy { it.computedMinAllocationWidth } + conversion.paddingWidth
                else -> conversion.attributes.width
            }.coerceAtMost(conversion.attributes.maxWidth) + conversion.marginWidth
        } else {
            when (conversion.attributes.width) {
                // = non limited width, = all item in single row is also ok
                RuiSizing.FillParent -> sizeFactorChildren.sumBy { it.computedCrossAxisItemWidth } + conversion.paddingWidth // actually is same
                RuiSizing.FitContent -> sizeFactorChildren.sumBy { it.computedCrossAxisItemWidth } + conversion.paddingWidth
                else -> conversion.attributes.width
            }.coerceAtMost(conversion.attributes.maxWidth) + conversion.marginWidth
        }

        conversion.computedCrossAxisItemHeight = if (isFlexRow) {
            when (conversion.attributes.height) {
                // = non limited height, = all item in single row is also ok
                RuiSizing.FillParent -> sizeFactorChildren.sumBy { it.computedMinAllocationHeight } + conversion.paddingHeight // actually is same
                RuiSizing.FitContent -> sizeFactorChildren.sumBy { it.computedMinAllocationHeight } + conversion.paddingHeight
                else -> conversion.attributes.height
            }.coerceAtMost(conversion.attributes.maxHeight) + conversion.marginHeight
        } else {
            when (conversion.attributes.height) {
                // = non limited height, = all item in single row is also ok
                RuiSizing.FillParent -> sizeFactorChildren.sumBy { it.computedCrossAxisItemHeight } + conversion.paddingHeight // actually is same
                RuiSizing.FitContent -> sizeFactorChildren.sumBy { it.computedCrossAxisItemHeight } + conversion.paddingHeight
                else -> conversion.attributes.height
            }.coerceAtMost(conversion.attributes.maxHeight) + conversion.marginHeight
        }

        conversion.computedMinAllocationWidth = when (conversion.attributes.width) {
            RuiSizing.FillParent -> 0
            RuiSizing.FitContent -> sizeFactorChildren.sumBy { it.computedMinAllocationWidth } + conversion.paddingWidth
            else -> conversion.attributes.width
        }.coerceAtLeast(conversion.attributes.minWidth) + conversion.marginWidth

        conversion.computedMinAllocationHeight = when (conversion.attributes.height) {
            RuiSizing.FillParent -> 0
            RuiSizing.FitContent -> sizeFactorChildren.sumBy { it.computedMinAllocationHeight } + conversion.paddingHeight
            else -> conversion.attributes.height
        }.coerceAtLeast(conversion.attributes.minHeight) + conversion.marginHeight
    }
    computeMaxMinSize(conversion)

    /**
     * return relativeParentCallback
     */
    fun computeFinalSize(
            conversion: InProgressConversion,
            promisedWidth: Int?,
            promisedHeight: Int?,
    ): (Int, Int) -> Unit {
        val isFlexRow = conversion.attributes.flexDirection == RuiFlexDirection.Row || conversion.attributes.flexDirection == RuiFlexDirection.RowReverse
        val isFlexReverse = conversion.attributes.flexDirection == RuiFlexDirection.RowReverse || conversion.attributes.flexDirection == RuiFlexDirection.ColumnReverse

        // assume we only have flex
        // split children into multiple rows
        val mainAxisMaxSize = if (isFlexRow) {
            when (conversion.attributes.width) {
                RuiSizing.FitContent -> promisedWidth ?: Int.MAX_VALUE
                RuiSizing.FillParent -> promisedWidth ?: Int.MAX_VALUE
                else -> conversion.attributes.width
            }.coerceAtMost(conversion.attributes.maxWidth)
        } else {
            when (conversion.attributes.height) {
                RuiSizing.FitContent -> promisedHeight ?: Int.MAX_VALUE
                RuiSizing.FillParent -> promisedHeight ?: Int.MAX_VALUE
                else -> conversion.attributes.height
            }.coerceAtMost(conversion.attributes.maxHeight)
        }

        val sizeFactorChildren = conversion.children.filter { it.isParentSizeFactor }

        var currentAxisContainer = FlexContainer(conversion.attributes.flexDirection, mainAxisMaxSize, isFlexReverse)
        var currentContainerReverse = isFlexReverse
        val mainAxisContainers = arrayListOf(currentAxisContainer)  // multiple line
        sizeFactorChildren.forEach {
            when (conversion.attributes.flexWrap) {
                RuiFlexWrap.Wrap -> when {
                    currentAxisContainer.shouldWarp(it) -> {
                        currentAxisContainer = FlexContainer(conversion.attributes.flexDirection, mainAxisMaxSize, currentContainerReverse)
                        currentAxisContainer.addChildren(it)
                        mainAxisContainers.add(currentAxisContainer)
                    }
                    else -> currentAxisContainer.addChildren(it)
                }
                RuiFlexWrap.WrapReverse -> when {
                    currentAxisContainer.shouldWarp(it) -> {
                        currentContainerReverse = !currentContainerReverse
                        currentAxisContainer = FlexContainer(conversion.attributes.flexDirection, mainAxisMaxSize, currentContainerReverse)
                        currentAxisContainer.addChildren(it)
                        mainAxisContainers.add(currentAxisContainer)
                    }
                    else -> currentAxisContainer.addChildren(it)
                }
                RuiFlexWrap.NoWrap -> currentAxisContainer.addChildren(it)
            }
        }

        conversion.flexContainers = mainAxisContainers

        val relativeSizeCallbacks = arrayListOf<(Int, Int) -> Unit>()
        mainAxisContainers.forEach {
            val freeSpace = it.mainAxisRemain.coerceAtLeast(0)
            val growChildrenAmount = it.children.count { it.attributes.flexGrow }
            val allocated = (0 until growChildrenAmount).map { (freeSpace / growChildrenAmount) }
                    .mapIndexed { index, value -> if (index + 1 <= freeSpace % growChildrenAmount) value + 1 else value }
            if (isFlexRow) {
                it.children.filter { child -> !child.attributes.flexGrow }.forEach { next ->
                    computeFinalSize(next, next.computedMinAllocationWidth, null).let { relativeSizeCallbacks.add(it) }
                }
                it.children.filter { child -> child.attributes.flexGrow }.forEachIndexed { index, next ->
                    val growed = (next.computedMinAllocationWidth + allocated[index]).coerceAtMost(next.attributes.maxWidth)
                    computeFinalSize(next, growed, null).let { relativeSizeCallbacks.add(it) }
                }
            } else {
                it.children.filter { child -> !child.attributes.flexGrow }.forEach { next ->
                    computeFinalSize(next, null, next.computedMinAllocationHeight).let { relativeSizeCallbacks.add(it) }
                }
                it.children.filter { child -> child.attributes.flexGrow }.forEachIndexed { index, next ->
                    val growed = (next.computedMinAllocationHeight + allocated[index]).coerceAtMost(next.attributes.maxHeight)
                    computeFinalSize(next, null, growed).let { relativeSizeCallbacks.add(it) }
                }
            }
        }



        if (isFlexRow) {
            conversion.finalWidth = mainAxisMaxSize
            conversion.finalHeight = kotlin.math.max(mainAxisContainers.asSequence().map { it.actualCrossSize }.maxOrNull()
                    ?: 0 + conversion.paddingHeight, conversion.computedCrossAxisItemHeight) // TODO: this should not be 0 if height is specified
        } else {
            conversion.finalHeight = mainAxisMaxSize
            conversion.finalWidth = kotlin.math.max(mainAxisContainers.asSequence().map { it.actualCrossSize }.maxOrNull()
                    ?: 0 + conversion.paddingWidth, conversion.computedCrossAxisItemWidth)
        }


        return { relativeParentWidth, relativeParentHeight ->

            val (newRelativeWidth, newRelativeHeight) =
                    if (conversion.attributes.position == RuiPosition.Relative) conversion.finalWidth to conversion.finalHeight
                    else relativeParentWidth to relativeParentHeight

            relativeSizeCallbacks.forEach { it(newRelativeWidth, newRelativeHeight) }

            // calculate relative in another loop, after all element have
            conversion.children.filter { !it.isParentSizeFactor }.forEach {

                when (it.attributes.position) {
                    RuiPosition.Fixed -> computeFinalSize(it, uiWidth, uiHeight)(newRelativeWidth, newRelativeHeight)
                    RuiPosition.Absolute -> computeFinalSize(it, newRelativeWidth, relativeParentHeight)(newRelativeWidth, newRelativeHeight)
                    else -> throw IllegalStateException("")
                }
            }
        }

    }
    computeFinalSize(conversion, uiWidth, uiHeight)(uiWidth, uiHeight)

    /**
     * return lastRelativeConversionCallback
     */
    fun computeChildrenPosition(conversion: InProgressConversion, lastRelativeConversion: InProgressConversion) {
        val isFlexRow = conversion.attributes.flexDirection == RuiFlexDirection.Row || conversion.attributes.flexDirection == RuiFlexDirection.RowReverse
        val newLastRelativeConversion = if (conversion.attributes.position == RuiPosition.Relative) conversion else lastRelativeConversion
        val isWrappedFlex = conversion.flexContainers.size > 1
        var flexContainerStackingCrossSize = 0

        val finalSumOfCrossSize = if (isFlexRow) conversion.finalHeight else conversion.finalWidth
        val allocatedFlexContainersCrossAxisSize = conversion.flexContainers
                .map { finalSumOfCrossSize / conversion.flexContainers.size }
                .mapIndexed { index, value -> if (index + 1 <= finalSumOfCrossSize % conversion.flexContainers.size) value + 1 else value }
        val flexContainersCrossStartPositions = allocatedFlexContainersCrossAxisSize.toIntArray().let { tmp ->
            tmp.forEachIndexed { i, it ->
                if (i > 0) tmp[i] += tmp[i - 1]
            }
            (listOf(0) + tmp.toList()).dropLast(1)
        }

        conversion.flexContainers.forEachIndexed { index, flexContainer ->

            val finalFlexContainerCrossAxisSize = allocatedFlexContainersCrossAxisSize[index]
            val finalFlexContainerCrossPosition = flexContainersCrossStartPositions[index]

            val mainAxisSpaceRemain = flexContainer.actualMainSize - (if (isFlexRow) conversion.finalWidth else conversion.finalHeight)

            val mainAxisSkip = when (conversion.attributes.justifyContent) {
                RuiJustifyContent.FlexStart -> 0
                RuiJustifyContent.FlexEnd -> mainAxisSpaceRemain
                RuiJustifyContent.Center -> mainAxisSpaceRemain / 2
            }


            var mainAxisPosition = mainAxisSkip + if (isFlexRow) conversion.finalUIX else conversion.finalUIY

            flexContainer.children.forEach { flexItem ->
                val crossAxisSpaceRemain = finalFlexContainerCrossAxisSize - (if (isFlexRow) flexItem.finalHeight else flexItem.finalWidth)

                val align = when (flexItem.attributes.alignSelf) {
                    RuiAlign.Default -> conversion.attributes.alignItem
                    else -> flexItem.attributes.alignSelf
                }

                val crossAxisPosition = when (align) {
                    RuiAlign.Default -> 0
                    RuiAlign.FlexStart -> 0
                    RuiAlign.FlexEnd -> crossAxisSpaceRemain
                    RuiAlign.Center -> crossAxisSpaceRemain / 2
                } + (if (isFlexRow) conversion.finalUIY else conversion.finalUIX) + finalFlexContainerCrossPosition

                if (isFlexRow) {
                    flexItem.finalUIX = mainAxisPosition
                    flexItem.finalUIY = crossAxisPosition
                } else {
                    flexItem.finalUIY = mainAxisPosition
                    flexItem.finalUIX = crossAxisPosition
                }
                computeChildrenPosition(flexItem, newLastRelativeConversion)

                mainAxisPosition += if (isFlexRow) flexItem.finalWidth else flexItem.finalHeight
            }
        }

        conversion.children.filter { !it.isParentSizeFactor }.forEach {
            when (it.attributes.position) {
                RuiPosition.Fixed -> calculateSpecialPositioning(it, 0, 0, uiWidth, uiHeight)
                RuiPosition.Absolute -> calculateSpecialPositioning(it,
                        newLastRelativeConversion.finalUIX,
                        newLastRelativeConversion.finalUIY,
                        newLastRelativeConversion.finalWidth,
                        newLastRelativeConversion.finalHeight)
                else -> throw java.lang.IllegalStateException()
            }
        }
    }
    calculateSpecialPositioning(conversion, 0, 0, uiWidth, uiHeight)
    computeChildrenPosition(conversion, conversion)

    val visualBoxes = ArrayList<VisualBox>()
    fun toVisualBox(conversion: InProgressConversion, inheritInteractEvent: RuiInteractEvent) {
        val overridedInteractEvent = if (conversion.attributes.interactEvent == RuiInteractEvent.Inherit) inheritInteractEvent else conversion.attributes.interactEvent
        visualBoxes.add(VisualBox(
                conversion.attributes.zIndex,
                conversion.finalUIX - 1,
                conversion.finalUIY - 1,
                conversion.finalUIX + conversion.finalWidth,
                conversion.finalUIY + conversion.finalHeight,
                if (overridedInteractEvent == RuiInteractEvent.None) null else conversion.eventTarget,
                if (conversion.attributes.visibility == RuiVisibility.Hidden) { _: BackgroundRenderingInfo -> null } else conversion.attributes.background
        ))
        conversion.children.forEach { toVisualBox(it, overridedInteractEvent) }
    }

    toVisualBox(conversion, RuiInteractEvent.Auto)
    return visualBoxes
}


class RuiPixel(
        var itemStack: ItemStack? = null,
        var eventTarget: ElementDOMTreeNode? = null
)

fun convertBoxesToPixes(visualBoxes: List<VisualBox>, height: Int, width: Int): HashMap<Pair<Int, Int>, RuiPixel> {
    val paintingResult: HashMap<Pair<Int, Int>, RuiPixel> = (0 until width)
            .flatMap { x -> (0 until height).map { y -> (x to y) to RuiPixel() } }.toMap(HashMap())
    val commitedResult: HashMap<Pair<Int, Int>, RuiPixel> = HashMap()

    visualOrderTreeNodes(visualBoxes)
            .toList().asReversed()
            .forEach { visualBox ->
                visualBox.pixels
                        .mapNotNull { position -> paintingResult[position]?.let { pixel -> position to pixel } }
                        .forEach { (position, pixel) ->
                            val (uiX, uiY) = position
                            val innerX = uiX - visualBox.x1 + 1
                            val innerY = uiY - visualBox.y1 + 1
                            if (visualBox.eventTarget != null && pixel.eventTarget == null) pixel.eventTarget = visualBox.eventTarget
                            if (pixel.itemStack == null) {
                                pixel.itemStack = visualBox.background(BackgroundRenderingInfo(uiX, uiY, innerX, innerY, visualBox.width, visualBox.height))
                            }
                            if (pixel.eventTarget != null && pixel.itemStack != null) {
                                commitedResult[position] = paintingResult.remove(position)!!

                                // if all painted
                                if (paintingResult.size == 0) return commitedResult
                            }
                        }
            }
    return commitedResult
}


fun visualOrderTreeNodes(visualBoxes: List<VisualBox>): Sequence<VisualBox> {
    return visualBoxes
            .groupBy { it.zIndex }.asSequence()
            .sortedBy { it.key }
            .flatMap { it.value }
}

fun RuiRootUI.reactantFrontRender(width: Int, height: Int) =
        constructInProgressConversion(this.rootRenderedTreeNode!!)
                .let { convertTreeToVisualBoxes(it!!, width, height) }
                .let { convertBoxesToPixes(it, height, width) }
