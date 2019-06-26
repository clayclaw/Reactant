package io.reactant.reactant.core.dependency

import io.reactant.reactant.core.ReactantCore
import io.reactant.reactant.core.dependency.injection.InjectRequirement
import io.reactant.reactant.core.dependency.injection.producer.InjectableWrapper
import io.reactant.reactant.core.dependency.injection.producer.ProvideInjectableWrapper
import io.reactant.reactant.core.dependency.injection.producer.ReactantObjectInjectableWrapper
import io.reactant.reactant.core.reactantobj.container.Reactant
import kotlin.reflect.jvm.jvmErasure

@Reactant
class DependencyManager {
    private val _dependencies = HashSet<InjectableWrapper>()
    val dependencies: Set<InjectableWrapper> get() = _dependencies
    val dependencyRelationManager = ReactantCore.instance.reactantInstanceManager.getOrConstructWithoutInjection(DependencyRelationManager::class)

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

        val reactantObjectInjectableMap = _dependencies.filter { it is ReactantObjectInjectableWrapper<*> }
                .map { it.productType.jvmErasure to it as ReactantObjectInjectableWrapper<*> }.toMap()

        reactantObjectInjectableMap.values.forEach(this::decideReactantObjectRequirementSolution)
    }

    /**
     * Decide and mark in wrapper as resolved
     */
    private fun decideReactantObjectRequirementSolution(reactantObjectWrapper: ReactantObjectInjectableWrapper<*>) {
        if (reactantObjectWrapper.fulfilled) return;
        reactantObjectWrapper.notFulfilledRequirements
                .mapNotNull { requirement -> fulfillRequirement(requirement)?.also { reactantObjectWrapper.resolvedRequirements[requirement] = it } }
                .toSet()
                .let { dependencyRelationManager.addDependencyRelation(reactantObjectWrapper, it) }
    }

    fun fulfillRequirement(requirement: InjectRequirement): InjectableWrapper? {
        val fulfillingDependencies = _dependencies
                .filter { it.canProvideType(requirement.requiredType) } // type match
                .filter { it.namePattern.toRegex().matches(requirement.name) } // name match
        // todo: decider
        if (fulfillingDependencies.size > 1)
            io.reactant.reactant.core.ReactantCore.logger.error("There have more than one injectables providing for ${requirement.requiredType}(name: ${requirement.name})," +
                    " ${fulfillingDependencies.map { "${it.productType}(NamePattern:${it.namePattern})" }}")

        return fulfillingDependencies.firstOrNull()
    }
}
