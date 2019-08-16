package dev.reactant.reactant.ui.kits.container

import dev.reactant.reactant.ui.element.ElementDisplay
import dev.reactant.reactant.ui.element.UIElement
import dev.reactant.reactant.ui.rendering.ElementSlot
import dev.reactant.reactant.ui.rendering.RenderedItems

class ContainerRendering(private val element: ReactantUIContainerElement,
                         private val parentFreeSpaceWidth: Int,
                         private val parentFreeSpaceHeight: Int) {
    private val marginX = Math.max(0, element.marginLeft) + Math.max(0, element.marginRight)
    private val marginY = Math.max(0, element.marginTop) + Math.max(0, element.marginBottom)
    private val freeSpaceWidth = Math.max(0, getBoxSize().first - element.paddingLeft - element.paddingRight)
    private val freeSpaceHeight = Math.max(0, getBoxSize().second - element.paddingTop - element.paddingBottom)
    val result = RenderedItems(
            (0 until getBoxSize().first)
                    .flatMap { x -> (0 until getBoxSize().second).map { x to it } }
                    .map { pos -> pos to ElementSlot.EMPTY }
                    .toMap().let { HashMap(it) }
    )

    private var nextChildPosX = 0;
    private var nextChildPosY = 0;
    private var currentLineMaxHeight = 1;

    /**
     * Get the size of element including padding but not margin
     */
    private fun getBoxSize(): Pair<Int, Int> {
        val freeSpaceWidth = when (element.width) {
            UIElement.WRAP_CONTENT -> 0  // as small as possible
            UIElement.MATCH_PARENT -> parentFreeSpaceWidth - marginX
            else -> element.width
        }
        val freeSpaceHeight = when (element.height) {
            UIElement.WRAP_CONTENT -> 0  // as small as possible
            UIElement.MATCH_PARENT -> parentFreeSpaceHeight - marginY
            else -> element.height
        }
        return freeSpaceWidth to freeSpaceHeight
    }

    private fun renderBackground() {
        (0 until getBoxSize().first)
                .flatMap { x -> (0 until getBoxSize().second).map { x to it } }
                .filter { !result.items.containsKey(it) || result.items[it] == ElementSlot.EMPTY }
                .forEach { result.items[it] = ElementSlot(element, element.getBackgroundItemStack(it.first, it.second)) }
    }

    private fun lineBreak() {
        nextChildPosX = element.paddingLeft;
        nextChildPosY += currentLineMaxHeight;
        currentLineMaxHeight = 1
    }

    init {
        element.children.forEach(this::renderChildren)
        hideOverflow()
        renderBackground() //todo handle background overflow
    }

    private fun hideOverflow() {
        if (element.overflowHidden) {
            val cuttingWidth = if (element.width == UIElement.WRAP_CONTENT) 10000 else freeSpaceWidth
            val cuttingHeight = if (element.height == UIElement.WRAP_CONTENT) 10000 else freeSpaceHeight

            result.hideOverflow(element.paddingLeft, element.paddingTop, cuttingWidth, cuttingHeight)
        }
    }

    private fun renderChildren(children: UIElement) {
        val displayBlockCuaseLineBreak = children.display == ElementDisplay.BLOCK && nextChildPosX != 0

        val needLineWrap = nextChildPosX != 0 // not start of line
                && children.minimumFreeSpaceWidth + element.paddingLeft + element.paddingRight + nextChildPosX > freeSpaceWidth
        if (displayBlockCuaseLineBreak || needLineWrap) lineBreak()

        val childPossibleWidth = freeSpaceWidth - nextChildPosX
        val childPossibleHeight = freeSpaceHeight - nextChildPosY
        val rendered = children.render(childPossibleWidth, childPossibleHeight)

        result.merge(rendered, nextChildPosX + element.paddingLeft, nextChildPosY + element.paddingTop)

        // used to locate next element
        val heightPlaceholderLimit = if (children.height > 0) children.height else 10000
        val widthPlaceholderLimit = if (children.width > 0) children.width else 10000
        currentLineMaxHeight = Math.max(currentLineMaxHeight, Math.min(rendered.totalHeight, heightPlaceholderLimit))
        nextChildPosX += Math.min(rendered.totalWidth, widthPlaceholderLimit)
        if (children.display == ElementDisplay.BLOCK) lineBreak()
    }
}
