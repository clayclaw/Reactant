package dev.reactant.reactant.ui

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.service.spec.dsl.register
import dev.reactant.reactant.service.spec.server.EventService
import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIEditing
import io.reactivex.disposables.Disposable
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory

@Component
class ReactantUIService(
        val event: EventService,
        val schedulerService: SchedulerService
) : LifeCycleHook {
    val inventoryUIMap = HashMap<Inventory, UIView>()
    val destroyOnNoViewer = HashMap<Inventory, UIView>()
    val autoDestroy = HashSet<UIView>()

    val pendingDestroy = HashMap<UIView, Disposable>()

    override fun onEnable() {
        register(event) {

            InventoryOpenEvent::class.observable(EventPriority.LOWEST)
                    .filter { !it.isCancelled && destroyOnNoViewer.containsKey(it.inventory) } // is pending destroy
                    .subscribe { destroyOnNoViewer.remove(it.inventory) }

            InventoryCloseEvent::class.observable(EventPriority.HIGHEST)
                    .filter { it.view.topInventory.viewers.size == 1 }  // only 1 viewer
                    .filter { inventoryUIMap.containsKey(it.view.topInventory) }  // it is ui view
                    .map { inventoryUIMap[it.view.topInventory]!! }
                    .filter { autoDestroy.contains(it) } // auto destroy available
                    .subscribe { pendingDestroy[it] = schedulerService.timer(20).subscribe { destroyUI(it) } }
        }
    }

    override fun onDisable() {
        inventoryUIMap.forEach { it.value.inventoryViews.values.forEach { it.close() } }
    }

    fun destroyUI(uiView: UIView) {
        uiView.destroy()
        inventoryUIMap.remove(uiView.inventory)
        pendingDestroy.remove(uiView)
        autoDestroy.remove(uiView)
    }

    fun showUI(uiView: UIView, player: Player) {
        if (!inventoryUIMap.containsKey(uiView.inventory)) throw IllegalStateException("View was destroyed")
        player.openInventory(uiView.inventory)
//        player.updateInventory()
    }

    fun createUI(initialViewer: Player, title: String, height: Int = 6, destroyUIOnNoViewer: Boolean = true, creating: ReactantUIEditing.() -> Unit): ReactantUIView {
        val ui = ReactantUIView(schedulerService, this::showUI, title, height)
        if (destroyUIOnNoViewer) autoDestroy += ui
        ReactantUIEditing(ui).apply(creating)
        inventoryUIMap[ui.inventory] = ui

        ui.show(initialViewer)

        return ui
    }

}

