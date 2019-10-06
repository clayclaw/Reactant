package dev.reactant.reactant.core.dependency

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import dev.reactant.reactant.core.dependency.injection.producer.DynamicProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import kotlin.reflect.jvm.jvmErasure

@Component
class ProviderManager {
    private val _providers = HashSet<Provider>()
    private val _blacklistedProviders = HashSet<Provider>()
    val blacklistedProviders get() = _blacklistedProviders
    val providers: Set<Provider> get() = _providers
    val providerRelationManager = ReactantCore.instance.reactantInstanceManager.getOrConstructWithoutInjection(ProviderRelationManager::class)

    fun addBlacklistedProvider(provider: Provider) {
        _blacklistedProviders.add(provider)
    }

    fun addProvider(provider: Provider) {
        _providers.add(provider)
    }

    fun removeProvider(provider: Provider) {
        _providers.remove(provider)
    }

    /**
     * Let dependency manager to start decide the relation between dependency using current dependency
     * Once a relation resolved and confirmed, the relation won't change anymore
     */
    fun decideRelation() {
        // Injectable provided by @Provide is directly required its' provider
        // ProvidedInjectable will not Inject dependency from outside
        _providers.filter { it is DynamicProvider<*, *> }
                .map { it as DynamicProvider<*, *> }
                .forEach {
                    providerRelationManager.addDependencyRelation(it, hashSetOf(it.providedInWrapper))
                }

        val componentInjectableMap = _providers.filter { it is ComponentProvider<*> }
                .map { it.productType.jvmErasure to it as ComponentProvider<*> }.toMap()

        componentInjectableMap.values.forEach(this::decideComponentRequirementSolution)
    }

    /**
     * Decide and mark in wrapper as resolved
     */
    private fun decideComponentRequirementSolution(componentProvider: ComponentProvider<*>) {
        if (componentProvider.fulfilled) return;
        componentProvider.notFulfilledRequirements
                .mapNotNull { requirement -> fulfillRequirement(requirement)?.also { componentProvider.resolvedRequirements[requirement] = it } }
                .toSet()
                .let { providerRelationManager.addDependencyRelation(componentProvider, it) }
    }

    fun fulfillRequirement(requirement: InjectRequirement): Provider? {
        val fulfillingDependencies = _providers
                .filter { it.canProvideType(requirement.requiredType) } // type match
                .filter { it.namePattern.toRegex().matches(requirement.name) } // name match
        // todo: decider
        if (fulfillingDependencies.size > 1)
            ReactantCore.logger.error("There have more than one injectables providing for ${requirement.requiredType}(name: ${requirement.name})," +
                    " ${fulfillingDependencies.map { "${it.productType}(NamePattern:${it.namePattern})" }}")

        return fulfillingDependencies.firstOrNull()
    }
}
