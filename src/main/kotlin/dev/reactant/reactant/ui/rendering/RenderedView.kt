package dev.reactant.reactant.ui.rendering

import dev.reactant.reactant.ui.UIView
import dev.reactant.reactant.ui.element.UIElement
import org.bukkit.inventory.ItemStack

interface RenderedView {
    val view: UIView

    /**
     * A 2d list which represent the rendered items in different position in the view
     */
    val result: HashMap<Pair<Int, Int>, ItemStack>

    /**
     * A 2d list which represent the elements layer in different position in the view
     */
    val layerResult: HashMap<Pair<Int, Int>, out List<UIElement>>
}
