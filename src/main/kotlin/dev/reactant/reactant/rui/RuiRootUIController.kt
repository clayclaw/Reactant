package dev.reactant.reactant.rui

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.rui.frontrender.reactantFrontRender
import dev.reactant.rui.RuiRootUI
import dev.reactant.rui.render.Update
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

private data class ReactantRuiRootUIState(
        val rootUI: RuiRootUI,
        var lastRenderedInventory: Inventory,
        val size: Int,
        val title: String,
)

@Component
class RuiRootUIController : LifeCycleHook {
    private val rootUIUpdateSubscriptions: HashMap<RuiRootUI, Disposable> = HashMap()
    private val inventoryRootUI: HashMap<Inventory, RuiRootUI> = HashMap()

    private val rootUIState: HashMap<RuiRootUI, ReactantRuiRootUIState> = HashMap()

    fun createRootUI(size: Int, title: String): RuiRootUI = RuiRootUI().apply {
        rootUIState[this] = ReactantRuiRootUIState(this, Bukkit.createInventory(null, size, title), size, title)
        rootUIUpdateSubscriptions[this] = batchedUpdates.subscribe { rerenderRootUI(this, it) }
    }

    private fun rerenderRootUI(rootUI: RuiRootUI, list: List<Update>? = listOf()) {
        rootUI.rerender(list)
        updateRenderedInventory(rootUI)
    }

    private fun updateRenderedInventory(rootUI: RuiRootUI) {
        rootUIState[rootUI]!!.lastRenderedInventory.let { inventory ->
            rootUI.reactantFrontRender(9, 6).let { pixels ->
                pixels.forEach { (x, y), pixel ->
                    inventory.setItem(x + y * 9, pixel.itemStack)
                }
            }
        }
    }

    private fun checkRootUI(rootUI: RuiRootUI) {
        if (!rootUIState.containsKey(rootUI)) throw IllegalStateException("The root ui is not controlled by Reactant")
    }

    fun showUI(rootUI: RuiRootUI, player: Player) {
        checkRootUI(rootUI)
        player.openInventory(rootUIState[rootUI]!!.lastRenderedInventory)
    }

    fun destroyUI(rootUI: RuiRootUI) {
        checkRootUI(rootUI)
        rootUIUpdateSubscriptions.remove(rootUI)!!.dispose()
        rootUIState.remove(rootUI)!!.lastRenderedInventory.viewers.forEach { it.closeInventory() }
    }

    fun getRootUIByInventory(inventory: Inventory) = inventoryRootUI[inventory]
}

