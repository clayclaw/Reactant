package dev.reactant.reactant.ui.rendering

import dev.reactant.reactant.ui.ReactantUIView
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.utils.content.item.itemStackOf
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ReactantRenderedView(override val view: ReactantUIView) : RenderedView {
    override var result: HashMap<Pair<Int, Int>, ItemStack> = hashMapOf()
    override var layerResult: HashMap<Pair<Int, Int>, ArrayList<ReactantUIElement>> = hashMapOf()

    init {

        view.rootElement.renderVisibleElementsPositions().forEach { (visibleEl, positions) ->
            positions.forEach { position -> layerResult.getOrPut(position, { arrayListOf() }).add(visibleEl) }
        }

        (0 until view.height).flatMap { row -> (0 until view.width).map { col -> col to row /* x, y */ } }
                .onEach { position -> this.layerResult[position]?.sortBy { it.computedZIndex } }
                .forEach { position ->
                    result[position] = (this.layerResult[position]
                            ?: listOf<ReactantUIElement>())
                            .map { it.render(it.getRelativePosition(position)) }
                            .last { it != null } ?: itemStackOf(Material.AIR)
                }

    }

}
