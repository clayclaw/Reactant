package dev.reactant.rui.render

import dev.reactant.rui.RuiRootUI
import dev.reactant.rui.dom.Props
import dev.reactant.rui.dom.RuiComponentElement
import dev.reactant.rui.dom.RuiElement
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

class RuiRenderGlobalState(
        // create a new list when start handling ui event
        // end the list and do batch update
        var pendingBatchUpdate: LinkedList<Update>? = LinkedList(),
        var currentNodeState: RuiNodeState? = null,
)

var ruiRenderGlobalState: RuiRenderGlobalState? = null

data class Update(
        val valueMutation: (ArrayList<Any?>) -> Unit,
        val targetNodeState: RuiNodeState
)

// we use delay rendering

class RuiNodeState(
        val ruiNode: RuiElement<*>,
        var originalStates: ArrayList<Any?> = arrayListOf(),
        var states: ArrayList<Any?> = arrayListOf(),
        // must be reset to 0 before start rendering the component
        var usedStateCount: Int = 0, // TODO: check this count at render done, if less than state length = usestate count changed
        var renderedChildren: List<RuiNodeState> = listOf(), // should no component element here
        var cachedDOMNode: ElementDOMTreeNode? = null, // should no component here
        var originalProps: Props?,
        var rootUI: RuiRootUI?,
        var unmountActions: ArrayList<() -> Unit> = arrayListOf(),
        var domRenderedAction: ArrayList<() -> Unit> = arrayListOf(),
        var parentNodeState: RuiNodeState?
) {
    val depth: Int by lazy { parentNodeState?.depth?.let { it + 1 } ?: 0 }
    fun isParent(nodeState: RuiNodeState): Boolean {
        if (parentNodeState == nodeState) return true
        return parentNodeState?.isParent(nodeState) ?: false
    }

    fun prepareRenderState() {
        this.unmountActions.clear()
        this.domRenderedAction.clear()
        this.usedStateCount = 0

        this.originalStates = this.states
        this.originalProps = this.ruiNode.props
        this.states = ArrayList(this.states)
    }
}

class ElementDOMTreeNode(
        val element: RuiElement<out Props>,
        val children: ArrayList<ElementDOMTreeNode> = arrayListOf(),
        val parent: ElementDOMTreeNode?,
        val indexAtParent: Int?,
) {

    fun printType(): String {
        return element.printType()
    }

    fun print(): String {
        val attr = element.props.toString().let { " ${it.take(100)}${if (it.length > 100) "..." else ""}" } ?: ""
        if (children.isNotEmpty() ?: false) {
            return """
<${printType()}${attr}>
${children.flatMap { it.print().split("\n") }.map { "    ${it}" }.joinToString("\n")}
</${printType()}>
                """.trimIndent()
        } else {
            return "<${printType()}${attr} />"
        }
    }
}

fun unmount(nodeState: RuiNodeState) {
    nodeState.rootUI = null
    nodeState.renderedChildren.forEach { unmount(it) }
    nodeState.unmountActions.forEach { it() }
    nodeState.parentNodeState = null
}

fun rerenderNode(currentNodeState: RuiNodeState, parentTreeNode: ElementDOMTreeNode?, domNodeIndex: Int?): ElementDOMTreeNode {
    currentNodeState.rootUI!!.let { it ->

        val isComponent = currentNodeState.ruiNode is RuiComponentElement<*>
        val needUpdate = when {
            !isComponent -> true
            currentNodeState.cachedDOMNode == null -> true
            currentNodeState.originalStates.size != currentNodeState.states.size -> true
            currentNodeState.originalStates.zip(currentNodeState.states).any { (a, b) -> a != b } -> true
            currentNodeState.originalProps == null -> true
            !currentNodeState.originalProps!!.equals(currentNodeState.ruiNode.props) -> true
            else -> false
        }

        return if (!needUpdate) currentNodeState.cachedDOMNode!! else {
            ruiRenderGlobalState!!.currentNodeState = currentNodeState

            currentNodeState.prepareRenderState()

            @Suppress("UNCHECKED_CAST")
            val element = when {
                // component is not element, we need to construct it
                isComponent -> (currentNodeState.ruiNode as RuiComponentElement<Props>).type(currentNodeState.ruiNode.props)
                else -> currentNodeState.ruiNode
            }

            // now we done update the element itself, let's take a look on their children

            val reusableChildren = LinkedHashSet(currentNodeState.renderedChildren)
            val reusableChildrenKeyMap = reusableChildren.filter { it.ruiNode.props.key != null }.map { it.ruiNode.props.key to it }.toMap()


            // Steps to reuse element ====
            // now we have find the same key element out and assigned them to the same key new element
            val newRenderedChildrenElementMap = element.props.children.map {
                it to reusableChildrenKeyMap[it.props.key]?.also { reusableChildren.remove(it) }
            }.toMap(LinkedHashMap())


            // for those no key element, we try to reuse the element
            val reusableChildrenTypeMap = HashMap<Any, LinkedList<RuiNodeState>>()
            reusableChildren.forEach {
                reusableChildrenTypeMap.getOrPut(it.ruiNode.type) { LinkedList<RuiNodeState>() }.add(it)
            }

            newRenderedChildrenElementMap.filter { it.value == null }.forEach { (needed) ->
                val firstMatch = reusableChildrenTypeMap[needed.type]?.pollFirst()
                reusableChildren.remove(firstMatch)
                newRenderedChildrenElementMap[needed] = firstMatch
            }

            // End of reuse element ====

            // destroy reused children if the root type is different
            // https://reactjs.org/docs/reconciliation.html#gatsby-focus-wrapper
            newRenderedChildrenElementMap.forEach { (needed, reused) ->
                if (reused != null && needed.type != reused.ruiNode.type) {
                    reused.renderedChildren = listOf()
                }
            }


            // for those no reusable element, create a new state
            newRenderedChildrenElementMap.filter { it.value == null }.forEach { (needed) ->
                newRenderedChildrenElementMap[needed] = RuiNodeState(
                        needed, ArrayList(), ArrayList(), 0, listOf(), null, null, currentNodeState.rootUI, parentNodeState = currentNodeState
                )
            }
            // for those non-reused element, unmount them
            reusableChildren.forEach(::unmount)

            val result = ElementDOMTreeNode(element, parent = parentTreeNode, indexAtParent = domNodeIndex)
            currentNodeState.renderedChildren = newRenderedChildrenElementMap.map { it.value!! }.toList()
            currentNodeState.renderedChildren.forEachIndexed { indexAtParent, childNodeState ->
                result.children.add(rerenderNode(childNodeState, result, indexAtParent))
            }
            currentNodeState.domRenderedAction.forEach { it() }
            currentNodeState.cachedDOMNode = result
            result
        }
    }
}

/**
 * @param isInitial should the render run at least 1 time
 */
fun renderUI(rootUI: RuiRootUI, isInitial: Boolean, batchedUpdates: List<Update>? = listOf()) {
    var rerenderCount = 0
    var internalBatchedUpdates: List<Update>? = batchedUpdates
    while ((isInitial && rerenderCount == 0) || internalBatchedUpdates!!.isNotEmpty()) {
        val isFirstRun = (isInitial && rerenderCount == 0)
        rerenderCount++

        if (ruiRenderGlobalState != null) throw java.lang.IllegalStateException("Who set that state? it should be null!")
        ruiRenderGlobalState = RuiRenderGlobalState()
        if (rerenderCount > 25) throw java.lang.IllegalStateException("Too many rerender, probably infinity loop")

        ruiRenderGlobalState!!.pendingBatchUpdate = LinkedList()

        // nothing updated

        if (isFirstRun) {
            rootUI.rootRenderedTreeNode = rerenderNode(rootUI.rootNodeState!!, null, null)
        } else {

            val needRerenderRuiStateNodes = HashSet<RuiNodeState>()
            internalBatchedUpdates?.filter { it.targetNodeState.rootUI == rootUI }?.forEach {
                it.valueMutation(it.targetNodeState.states)
                needRerenderRuiStateNodes.add(it.targetNodeState)
            }

            // optimize update list, ignore unnecessary children update
            needRerenderRuiStateNodes
                    .groupBy { it.depth }.entries
                    .minByOrNull { it.key }?.value
                    ?.forEach { nodeState ->
                        rerenderNode(rootUI.rootNodeState!!, nodeState.cachedDOMNode!!.parent, nodeState.cachedDOMNode!!.indexAtParent).let { renderedNode ->
                            if (renderedNode.parent == null) { // is root
                                rootUI.rootRenderedTreeNode = renderedNode
                            } else {
                                renderedNode.parent.children.set(renderedNode.indexAtParent!!, renderedNode)
                            }
                        }
                    }
        }

        if (ruiRenderGlobalState!!.pendingBatchUpdate?.isNotEmpty() == true) {
            internalBatchedUpdates = ruiRenderGlobalState!!.pendingBatchUpdate
        }
        ruiRenderGlobalState = null
    }
}
