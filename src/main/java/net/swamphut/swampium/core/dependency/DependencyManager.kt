package net.swamphut.swampium.core.dependency

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.dependency.injection.InjectRequirement
import net.swamphut.swampium.core.dependency.injection.producer.InjectableWrapper
import net.swamphut.swampium.core.dependency.injection.producer.ProvideInjectableWrapper
import net.swamphut.swampium.core.dependency.injection.producer.SwObjectInjectableWrapper
import net.swamphut.swampium.core.swobject.container.SwObject
import kotlin.reflect.jvm.jvmErasure

@SwObject
class DependencyManager {
    private val _dependencies = HashSet<InjectableWrapper>()
    val dependencies: Set<InjectableWrapper> get() = _dependencies
    val dependencyRelationManager = Swampium.instance.swInstanceManager.getOrConstructWithoutInjection(DependencyRelationManager::class)

    fun addDependency(injectableWrapper: InjectableWrapper) {
        _dependencies.add(injectableWrapper)
    }

    fun removeDependency(injectableWrapper: InjectableWrapper) {
        _dependencies.remove(injectableWrapper)
    }

    /**
     * Let dependency manager to start decide the relation between dependency using current dependency
     * Once a relation resolved and confirmed, the relation won't change anymore
     */
    fun decideRelation() {
        // Injectable provided by @Provide is directly required its' provider
        // ProvidedInjectable will not Inject dependency from outside
        _dependencies.filter { it is ProvideInjectableWrapper<*, *> }
                .map { it as ProvideInjectableWrapper<*, *> }
                .forEach {
                    dependencyRelationManager.addDependencyRelation(it, hashSetOf(it.providedInWrapper))
                }

        val swObjectInjectableMap = _dependencies.filter { it is SwObjectInjectableWrapper<*> }
                .map { it.productType.jvmErasure to it as SwObjectInjectableWrapper<*> }.toMap()

        swObjectInjectableMap.values.forEach(this::decideSwObjectRequirementSolution)
    }

    /**
     * Decide and mark in wrapper as resolved
     */
    private fun decideSwObjectRequirementSolution(swObjectWrapper: SwObjectInjectableWrapper<*>) {
        if (swObjectWrapper.fulfilled) return;
        swObjectWrapper.notFulfilledRequirements
                .mapNotNull { requirement -> fulfillRequirement(requirement)?.also { swObjectWrapper.resolvedRequirements[requirement] = it } }
                .toSet()
                .let { dependencyRelationManager.addDependencyRelation(swObjectWrapper, it) }
    }

    fun fulfillRequirement(requirement: InjectRequirement): InjectableWrapper? {
        val fulfillingDependencies = _dependencies
                .filter { it.canProvideType(requirement.requiredType) } // type match
                .filter { it.namePattern.toRegex().matches(requirement.name) } // name match
        // todo: decider
        if (fulfillingDependencies.size > 1)
            Swampium.logger.error("There have more than one injectables providing for ${requirement.requiredType}(name: ${requirement.name})," +
                    " ${fulfillingDependencies.map { "${it.productType}(NamePattern:${it.namePattern})" }}")

        return fulfillingDependencies.firstOrNull()
    }
}
