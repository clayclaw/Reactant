package net.swamphut.swampium.ui.kits.container

import net.swamphut.swampium.ui.creation.SwUIElementCreation
import net.swamphut.swampium.ui.element.SwUIElement
import net.swamphut.swampium.ui.element.type.spacing.PaddingElement
import net.swamphut.swampium.ui.element.type.spacing.PaddingElementCreation
import net.swamphut.swampium.ui.rendering.RenderedItems
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * The abstract class which can sort and render the children elements
 */
abstract class SwUIContainerElement(elementIdentifier: String) : SwUIElement(elementIdentifier), PaddingElement {
    /**
     * Hide child rendered overflow item if true
     * WRAP_CONTENT will ignore this option
     */
    var overflowHidden = true

    override var paddingTop: Int = 0
    override var paddingRight: Int = 0
    override var paddingBottom: Int = 0
    override var paddingLeft: Int = 0

    open fun getBackgroundItemStack(x: Int, y: Int): ItemStack = ItemStack(Material.AIR)


    override fun render(parentFreeSpaceWidth: Int, parentFreeSpaceHeight: Int): RenderedItems {
        return ContainerRendering(this, parentFreeSpaceWidth, parentFreeSpaceHeight).result
                .addMargin(this, parentFreeSpaceWidth, parentFreeSpaceHeight)
    }
}

abstract class SwUIContainerElementCreation<T : SwUIContainerElement>(element: T)
    : SwUIElementCreation<T>(element), PaddingElementCreation<T>
