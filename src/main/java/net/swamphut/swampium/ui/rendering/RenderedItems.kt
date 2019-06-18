package net.swamphut.swampium.ui.rendering

import org.bukkit.inventory.ItemStack

class RenderedItems(val items: HashMap<Pair<Int, Int>, ElementSlot> = HashMap()) {
    /**
     * The height of this rendered items will be extended (ignore negative y)
     */
    val heightExtend
        get() = items.map { it.key.second }
                .filter { it >= 0 }
                .fold(0) { sum, next -> Math.max(sum, next) } + 1

    /**
     * Merge all items in the rendered item into this container
     */
    fun merge(renderedItems: RenderedItems, mergeIntoX: Int, mergeIntoY: Int) {
        renderedItems.items.mapKeys { it.key.first + mergeIntoX to it.key.second + mergeIntoY }
                .forEach { items[it.key] = it.value }
    }

    fun filterOverflow(overflowPredicate: (Int, Int) -> Boolean) {
        items.keys.filter { (x, y) -> overflowPredicate(x, y) }.forEach { items.remove(it) }
    }

    fun filterOverflow(x: Int, y: Int, width: Int, height: Int) =
            filterOverflow { px, py -> px < x || px >= x + width || py < y || py >= y + height }
}