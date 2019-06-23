package net.swamphut.swampium.ui

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.service.spec.dsl.register
import net.swamphut.swampium.service.spec.server.EventService
import net.swamphut.swampium.service.spec.server.SchedulerService
import net.swamphut.swampium.ui.creation.SwUICreation
import net.swamphut.swampium.ui.event.interact.UIClickEvent
import net.swamphut.swampium.ui.event.interact.element.UIElementClickEvent
import net.swamphut.swampium.ui.event.inventory.UICloseEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory

@SwObject
class SwUIService(
        val event: EventService,
        val schedulerService: SchedulerService
) : LifeCycleHook {
    val inventoryUIMap = HashMap<Inventory, UIView>()
    val uiScheduler = HashMap<UIView, UIScheduler>()
    val destroyOnNoViewer = HashMap<Inventory, UIView>()
    val autoDestroy = HashSet<UIView>()

    val pendingDestroy = HashMap<UIView, Disposable>()

    override fun init() {
        register(event) {
            InventoryClickEvent::class.observable()
                    .filter { it.whoClicked is Player }
                    .filter { inventoryUIMap.containsKey(it.whoClicked.openInventory.topInventory) }
                    .doOnError { Swampium.logger.error(it) }
                    .forEach { bukkitEvent ->
                        val uiContainer = inventoryUIMap[bukkitEvent.whoClicked.openInventory.topInventory]!!

                        val isClickOnUIContainer = bukkitEvent.clickedInventory == uiContainer.inventory
                        val clickedElement = if (isClickOnUIContainer) uiContainer.getElementAt(bukkitEvent.slot) else null
                        when {
                            isClickOnUIContainer && clickedElement != null -> UIElementClickEvent(clickedElement, bukkitEvent).propagateTo(clickedElement)
                            else -> object : UIClickEvent {
                                override val bukkitEvent = bukkitEvent
                            }.propagateTo(uiContainer)
                        }
                    }

            InventoryDragEvent::class.observable()
            InventoryMoveItemEvent::class.observable()
            InventoryPickupItemEvent::class.observable()

            InventoryOpenEvent::class.observable(EventPriority.LOWEST)
                    .filter { !it.isCancelled && destroyOnNoViewer.containsKey(it.inventory) } // is pending destroy
                    .subscribe { destroyOnNoViewer.remove(it.inventory) }

            InventoryCloseEvent::class.observable(EventPriority.HIGHEST)
                    .filter { it.view.topInventory.viewers.size == 1 }  // only 1 viewer
                    .filter { inventoryUIMap.containsKey(it.view.topInventory) }  // it is ui view
                    .map { inventoryUIMap[it.view.topInventory]!! }
                    .filter { autoDestroy.contains(it) } // auto destroy available
                    .subscribe { pendingDestroy[it] = schedulerService.timer(20).subscribe { destroyUI(it) } }

            InventoryCloseEvent::class.observable()
                    .filter { inventoryUIMap.containsKey(it.view.topInventory) }  // it is ui view
                    .subscribe { inventoryUIMap[it.view.topInventory]!!.event.onNext(UICloseEvent(it)) }
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

    fun createUI(initialViewer: Player, destroyUIOnNoViewer: Boolean = true, creating: SwUICreation.() -> Unit): SwUIView {
        val allocatedScheduler = UIScheduler(schedulerService)
        val ui = SwUIView(allocatedScheduler, this::showUI)
        uiScheduler[ui] = allocatedScheduler
        inventoryUIMap[ui.inventory] = ui
        if (destroyUIOnNoViewer) autoDestroy += ui

        ui.show(initialViewer)
        SwUICreation(ui).apply(creating)
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

