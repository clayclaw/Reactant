package net.swamphut.swampium.core.swobject.dependency.resolve

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.swobject.dependency.ServiceProviderInfo
import net.swamphut.swampium.core.swobject.dependency.ServiceProviderManager
import net.swamphut.swampium.utils.types.DirectedAcyclicGraph


class ServiceDependencyResolver {
    companion object {
        fun resolve(resolvingProviders: Set<ServiceProviderInfo<Any>>): DirectedAcyclicGraph.SolveResult<ServiceProviderInfo<*>> {
            val serviceDAG = DirectedAcyclicGraph<ServiceProviderInfo<*>>()
            val serviceProviderManager = Swampium.instance.instanceManager.getInstance(ServiceProviderManager::class.java);

            resolvingProviders.forEach { resolving ->
                val resolvingNode = serviceDAG.getNodeOrAdd(resolving)
                resolving.requester
                        .map { serviceProviderManager.serviceClassProvidersInfoMap[it.instance.javaClass] }
                        .filter { it != null }
                        .map { it!! }
                        .forEach { requester ->
                            resolvingNode.addChild(serviceDAG.getNodeOrAdd(requester).also { it.addParent(resolvingNode) })
                        }

            }
            return serviceDAG.solveTopologicalOrdering()
        }

        fun reverseResolve(resolvingProviders: Set<ServiceProviderInfo<Any>>): DirectedAcyclicGraph.SolveResult<ServiceProviderInfo<*>> {
            val serviceDAG = DirectedAcyclicGraph<ServiceProviderInfo<*>>()
            val serviceProviderManager = Swampium.instance.instanceManager.getInstance(ServiceProviderManager::class.java);

            resolvingProviders.forEach { resolving ->
                val resolvingNode = serviceDAG.getNodeOrAdd(resolving)
                resolving.requester
                        .map { serviceProviderManager.serviceClassProvidersInfoMap[it.instance.javaClass] }
                        .filter { it != null }
                        .map { it!! }
                        .forEach { requester ->
                            resolvingNode.addChild(serviceDAG.getNodeOrAdd(requester).also { it.addParent(resolvingNode) })
                        }

            }
            return serviceDAG.solveTopologicalOrdering()
        }
    }
}
