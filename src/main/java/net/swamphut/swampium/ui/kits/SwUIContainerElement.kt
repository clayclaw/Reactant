package net.swamphut.swampium.ui.kits

import net.swamphut.swampium.ui.element.ElementDisplay
import net.swamphut.swampium.ui.element.SwUIElement
import net.swamphut.swampium.ui.rendering.ElementSlot
import net.swamphut.swampium.ui.rendering.RenderedItems
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * The abstract class which can sort and render the children elements
 */
abstract class SwUIContainerElement(elementIdentifier: String) : SwUIElement(elementIdentifier) {

    protected fun getBackgroundItemStack(x: Int, y: Int): ItemStack = ItemStack(Material.AIR)

    override fun render(): RenderedItems {
        val result = RenderedItems(
                (0..height).flatMap { x -> (0..width).map { x to it } }
                        .map { pos -> pos to ElementSlot(this, getBackgroundItemStack(pos.first, pos.second)) }
                        .toMap().let { HashMap(it) }
        )
        var x = 0;
        var y = 0;
        var lineMaxHeight = 1;
        fun lineBreak() {
            x = 0; y += lineMaxHeight; lineMaxHeight = 1
        }
        children.forEach {
            if (it.display == ElementDisplay.BLOCK && x != 0) lineBreak()
            val rendered = it.render()
            result.merge(rendered, x, y)
            lineMaxHeight = Math.max(lineMaxHeight, rendered.heightExtend)
        }
        result.filterOverflow(0, 0, width, height)
        return result
    }
}