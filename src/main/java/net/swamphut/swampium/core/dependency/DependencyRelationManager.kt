package net.swamphut.swampium.core.dependency

import net.swamphut.swampium.core.dependency.injectable.producer.InjectableProducerWrapper
import net.swamphut.swampium.core.dependency.injectable.producer.ProvideInjectableProducerWrapper
import net.swamphut.swampium.core.exception.CyclicDependencyRelationException
import java.lang.IllegalStateException

private typealias Dependency = InjectableProducerWrapper

/**
 * The dependencies loading order resolver
 */
class DependencyRelationManager {
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
}
