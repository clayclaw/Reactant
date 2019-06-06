package net.swamphut.swampium.core.dependency

import net.swamphut.swampium.core.Swampium
import net.swamphut.swampium.core.dependency.injectable.producer.InjectableProducerWrapper
import net.swamphut.swampium.core.dependency.injectable.producer.ProvideInjectableProducerWrapper
import net.swamphut.swampium.core.dependency.injectable.producer.SwObjectInjectableProducerWrapper
import net.swamphut.swampium.core.dependency.injection.InjectRequirement
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.jvmErasure

class DependencyManager {
    private val dependencies = HashSet<InjectableProducerWrapper>()
    val dependencyRelationManager = Swampium.instance.swampiumInstanceManager.getOrConstructWithoutInjection(DependencyRelationManager::class)

    fun addDependency(injectableProducerWrapper: InjectableProducerWrapper) {
        dependencies.add(injectableProducerWrapper)
    }

    fun getDependencyInstance(requestedType: KType, requestedName: String, requester: Any) {
        // todo
    }

    /**
     * Let dependency manager to start decide the relation between dependency using current dependency
     * Once a relation resolved and confirmed, the relation won't change anymore
     */
    fun decideRelation() {
        val swObjectInjectableMap = dependencies.filter { it is SwObjectInjectableProducerWrapper<*> }
                .map { it.productType.jvmErasure to it as SwObjectInjectableProducerWrapper<*> }.toMap()

        swObjectInjectableMap.values.forEach(this::decideSwObjectRequirementSolution)

        // Injectable provided by @Provide is directly required its' provider
        // ProvidedInjectable will not Inject dependency from outside
        dependencies.filter { it is ProvideInjectableProducerWrapper<*, *> }
                .map { it as ProvideInjectableProducerWrapper<*, *> }
                .forEach {
                    dependencyRelationManager.addDependencyRelation(it,
                            hashSetOf(swObjectInjectableMap[it.providedIn] as InjectableProducerWrapper))
                }
    }

    private fun decideSwObjectRequirementSolution(swObjectWrapper: SwObjectInjectableProducerWrapper<*>) {
        if (swObjectWrapper.fulfilled) return;
        swObjectWrapper.notFulfilledRequirements
                .mapNotNull { fulfillRequirment(it) }
                .toSet()
                .let { dependencyRelationManager.addDependencyRelation(swObjectWrapper, it) }
    }

    private fun fulfillRequirment(requirement: InjectRequirement): InjectableProducerWrapper? {
        val fulfillingDependencies = dependencies
                .filter { it.namePattern.toRegex().matches(requirement.name) } // name match
                .filter { it.productType.isSubtypeOf(requirement.requiredType) } // type match
        // todo: decider
        if (fulfillingDependencies.size > 1)
            Swampium.logger.error("There have more than one injectables providing for ${requirement.requiredType}(name: ${requirement.name})," +
                    " ${fulfillingDependencies.map { "${it.productType}(NamePattern:${it.namePattern})" }}")

        return fulfillingDependencies.firstOrNull()
    }
}
