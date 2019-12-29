package dev.reactant.reactant.ui.kits.slot.binding

import dev.reactant.reactant.service.spec.server.SchedulerService
import dev.reactant.reactant.ui.editing.ReactantUIElementEditing
import dev.reactant.reactant.ui.element.ReactantUIElement
import dev.reactant.reactant.ui.element.UIElementName
import dev.reactant.reactant.utils.content.item.itemStackOf
import dev.reactant.uikit.element.slot.ItemStorageElement
import dev.reactant.uikit.element.slot.ReactantUISlotElement
import dev.reactant.uikit.element.slot.ReactantUISlotElementEditing
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@UIElementName("bindingSlot")
open class ReactantUIBindingSlotElement(allocatedSchedulerService: SchedulerService)
    : ReactantUISlotElement(allocatedSchedulerService), ItemStorageElement {

    /**
     * The disposable of the binding subscription, it should be dispose when binding to new subject
     */
    protected var bindingDisposable: Disposable? = null
        set(value) {
            field?.let { compositeDisposable.remove(it);it.dispose() };
            field = value;
            value?.let { compositeDisposable.add(it) }
        }


    protected var bindingSubject: BehaviorSubject<ItemStack>? = null
        set(value) = kotlin.run { field = value }.also { subscribeItemSubject(value) }

    fun bind(subject: BehaviorSubject<ItemStack>?) {
        this.bindingSubject = subject
    }

    override var slotItem: ItemStack
        set(value) = run {
            bindingSubject?.onNext(value)
        }
        get() = bindingSubject?.value ?: itemStackOf(Material.AIR)

    private fun subscribeItemSubject(subject: BehaviorSubject<ItemStack>?) {
        bindingDisposable = subject?.let {
            it.subscribe { _ -> view?.render() }
        }
    }
}


open class ReactantUIBindingSlotElementEditing<out T : ReactantUIBindingSlotElement>(element: T)
    : ReactantUISlotElementEditing<T>(element) {
    fun bind(subject: BehaviorSubject<ItemStack>?) = element.bind(subject)
}

fun ReactantUIElementEditing<ReactantUIElement>.bindingSlot(creation: ReactantUIBindingSlotElementEditing<ReactantUIBindingSlotElement>.() -> Unit) {
    element.children.add(ReactantUIBindingSlotElement(element.allocatedSchedulerService)
            .also { ReactantUIBindingSlotElementEditing(it).apply(creation) })
}


