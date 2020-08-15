package dev.reactant.rui

import dev.reactant.rui.dom.RuiElement
import dev.reactant.rui.render.*
import io.reactivex.rxjava3.subjects.PublishSubject

class RuiRootUI(
        var rootNodeState: RuiNodeState? = null,
        var rootRenderedTreeNode: ElementDOMTreeNode? = null,
        val batchedUpdates: PublishSubject<List<Update>> = PublishSubject.create(),
) {
    fun rerender(batchedUpdates: List<Update>? = listOf()) {
        renderUI(this, false, batchedUpdates)
    }


    fun render(element: RuiElement<*>) {
        rootNodeState?.let { unmount(it) }
        rootNodeState = RuiNodeState(ruiNode = element, originalProps = null, rootUI = this, parentNodeState = null)
        renderUI(this, true)
        batchedUpdates.onNext(listOf()) // trigger rerender
    }
}
