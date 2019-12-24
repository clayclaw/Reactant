package dev.reactant.reactant.core.dependency

import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.exception.CyclicDependencyRelationException

private typealias Dependency = Provider

/**
 * The providers loading order resolver
 */
class ProviderRelationManager {
    private val nodes = HashMap<Dependency, Node>()

    inner class Node(val dependency: Dependency) {
        val requiredBy: HashSet<Node> = HashSet();
        val required: HashSet<Node> = HashSet();

        val isCyclic: Boolean get() = kotlin.runCatching { roots }.isFailure

        val roots: Set<Node> get() = findRoot(setOf())

        private fun findRoot(walkedNodes: Set<Node>): Set<Node> {
            val walkedBefore = walkedNodes.indexOf(this);
            if (walkedBefore != -1)
                throw CyclicDependencyRelationException(walkedNodes.drop(walkedBefore).map { it.dependency })

            return required.map { it.findRoot(walkedNodes.union(linkedSetOf(this))) }.flatten().toSet()
        }
    }

    fun addDependencyRelation(dependency: Dependency, required: Set<Dependency>) {
        val dependencyNode = nodes.getOrPut(dependency) { Node(dependency) }
        val requiredNodes = required.map { nodes.getOrPut(it) { Node(it) } }
        dependencyNode.required.addAll(requiredNodes);
        requiredNodes.forEach { it.requiredBy.add(dependencyNode) }
    }

    /**
     * Remove a dependency from manager, and remove requiredBy relation from its parents
     * @throws IllegalStateException If there have any other dependency which required it is still exist
     */
    fun removeDependency(dependency: Dependency) {
        val removingNode = nodes[dependency] ?: return;
        if (nodes.values.any { it.required.contains(removingNode) }) throw IllegalStateException();
        nodes.values.forEach { it.requiredBy.remove(removingNode) }
    }

    /**
     * Get all providers which requiring it
     */
    fun getDependencyChildrenRecursively(dependency: Dependency): Set<Dependency> = nodes[dependency]?.requiredBy
            ?.flatMap { setOf(it.dependency).union(getDependencyChildrenRecursively(it.dependency)) }
            ?.toSet() ?: setOf()

    fun getDependenciesDepth(): Map<Dependency, Int> {
        val depth = hashMapOf<Dependency, Int>()
        fun getDependencyDepth(dependency: Dependency, walked: LinkedHashSet<Dependency>): Int {
            if (walked.contains(dependency)) throw CyclicDependencyRelationException(arrayListOf(dependency).also { it.addAll(walked.reversed()) })
            walked.add(dependency)
            return (depth[dependency] // if cache exist
                    ?: nodes[dependency]!!.required.map { getDependencyDepth(it.dependency, LinkedHashSet(walked)) + 1 }.max() // from max depth + 1
                    ?: 0) // 0 if no required providers
        }
        nodes.keys.forEach {
            depth.set(it, getDependencyDepth(it, linkedSetOf()))
        }
        return depth
    }
}
