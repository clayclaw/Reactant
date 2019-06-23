package net.swamphut.swampium.ui.rendering

import net.swamphut.swampium.ui.element.UIElement
import net.swamphut.swampium.ui.element.UIElement.Companion.MARGIN_AUTO

class RenderedItems(var items: HashMap<Pair<Int, Int>, ElementSlot> = HashMap()) {
    /**
     * The width of this rendered items will be extended (ignore negative x)
     */
    val totalWidth
        get() = items.map { it.key.first }
                .filter { it >= 0 }
                .fold(0) { sum, next -> Math.max(sum, next) } + 1
    /**
     * The height of this rendered items will be extended (ignore negative y)
     */
    val totalHeight
        get() = items.map { it.key.second }
                .filter { it >= 0 }
                .fold(0) { sum, next -> Math.max(sum, next) } + 1


    /**
     * Merge all items in the rendered item into this container
     */
    fun merge(renderedItems: RenderedItems, mergeIntoX: Int, mergeIntoY: Int) {
        renderedItems.items.mapKeys { it.key.first + mergeIntoX to it.key.second + mergeIntoY }
                .filter { it.value != ElementSlot.EMPTY }
                .forEach { items[it.key] = it.value }
    }

    fun hideOverflow(overflowPredicate: (Int, Int) -> Boolean) {
        items.keys.filter { (x, y) -> overflowPredicate(x, y) }.forEach { items.remove(it) }
    }

    fun hideOverflow(x: Int, y: Int, width: Int, height: Int) {
        hideOverflow { px, py -> px < x || px >= x + width || py < y || py >= y + height }
    }

    fun addMargin(element: UIElement, parentFreeSpaceWidth: Int, parentFreeSpaceHeight: Int): RenderedItems {
        var remainWidth = parentFreeSpaceWidth - totalWidth
        var remainHeight = parentFreeSpaceHeight - totalHeight

        val marginLeft = Math.max(0, when {
            element.marginLeft == MARGIN_AUTO && element.marginRight == MARGIN_AUTO -> remainWidth / 2
            element.marginLeft == MARGIN_AUTO -> remainWidth - element.marginRight
            else -> element.marginLeft
        })
        remainWidth -= marginLeft
        val marginRight = Math.max(0, when {
            element.marginRight == MARGIN_AUTO -> remainWidth
            else -> element.marginRight
        })

        val marginTop = Math.max(0, when {
            element.marginTop == MARGIN_AUTO && element.marginBottom == MARGIN_AUTO -> remainHeight / 2
            element.marginTop == MARGIN_AUTO -> remainHeight - element.marginBottom
            else -> element.marginTop
        })
        remainHeight -= marginTop
        val marginBottom = Math.max(0, when {
            element.marginBottom == MARGIN_AUTO -> remainWidth
            else -> element.marginBottom
        })

        val withMarginWidth = marginLeft + marginRight + totalWidth
        val withMarginHeight = marginTop + marginBottom + totalHeight
        items = HashMap(items.mapKeys { it.key.first + marginLeft to it.key.second + marginTop })
        if (!items.containsKey(0 to 0)) items[0 to 0] = ElementSlot.EMPTY
        if (!items.containsKey(withMarginWidth - 1 to withMarginHeight - 1))
            items[withMarginWidth - 1 to withMarginHeight - 1] = ElementSlot.EMPTY
        return this
    }
}
