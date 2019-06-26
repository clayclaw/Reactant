package io.reactant.reactant.ui.kits.container

import io.reactant.reactant.ui.editing.ReactantUIElementEditing
import io.reactant.reactant.ui.element.ReactantUIElement
import io.reactant.reactant.ui.element.type.spacing.PaddingElement
import io.reactant.reactant.ui.element.type.spacing.PaddingElementEditing
import io.reactant.reactant.ui.rendering.RenderedItems
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * The abstract class which can sort and render the children elements
 */
abstract class ReactantUIContainerElement(elementIdentifier: String) : ReactantUIElement(elementIdentifier), PaddingElement {
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

abstract class ReactantUIContainerElementEditing<T : ReactantUIContainerElement>(element: T)
    : ReactantUIElementEditing<T>(element), PaddingElementEditing<T>
