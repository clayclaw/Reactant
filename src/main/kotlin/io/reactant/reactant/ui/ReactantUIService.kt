package io.reactant.reactant.ui

import io.reactant.reactant.core.component.Component
import io.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.reactant.reactant.service.spec.dsl.register
import io.reactant.reactant.service.spec.server.EventService
import io.reactant.reactant.service.spec.server.SchedulerService
import io.reactant.reactant.ui.editing.ReactantUIEditing
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
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
    val uiScheduler = HashMap<UIView, UIScheduler>()
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

    fun destroyUI(uiView: UIView) {
        inventoryUIMap.remove(uiView.inventory)
        uiScheduler.remove(uiView)?.compositeDisposable?.dispose()
        pendingDestroy.remove(uiView)
        autoDestroy.remove(uiView)
    }

    fun showUI(uiView: UIView, player: Player) {
        if (!inventoryUIMap.containsKey(uiView.inventory)) throw IllegalStateException("View was destroyed")
        player.openInventory(uiView.inventory)
//        player.updateInventory()
    }

    fun createUI(initialViewer: Player, height: Int = 6, destroyUIOnNoViewer: Boolean = true, creating: ReactantUIEditing.() -> Unit): ReactantUIView {
        val allocatedScheduler = UIScheduler(schedulerService)
        val ui = ReactantUIView(allocatedScheduler, this::showUI, height)
        uiScheduler[ui] = allocatedScheduler
        if (destroyUIOnNoViewer) autoDestroy += ui
        ReactantUIEditing(ui).apply(creating)
        inventoryUIMap[ui.inventory] = ui

        ui.show(initialViewer)
        ui.render()

        return ui
    }

    inner class UIScheduler(val schedulerService: SchedulerService) : SchedulerService {
        val compositeDisposable = CompositeDisposable()
        override fun next(): Completable = schedulerService.next().doOnSubscribe { compositeDisposable.add(it) }

        override fun timer(delay: Long): Completable = schedulerService.timer(delay).doOnSubscribe { compositeDisposable.add(it) }

        override fun interval(delay: Long, period: Long): Observable<Int> = schedulerService.interval(delay, period)
                .doOnSubscribe { compositeDisposable.add(it) }
    }
}

