package dev.reactant.reactant.core.dependency

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.dependency.relation.*

@Component
class ProviderManager {
    private val _providers = HashSet<Provider>()
    private val _blacklistedProviders = HashSet<Provider>()
    /**
     * Providers that was been blacklisted by config
     */
    val blacklistedProviders get() = _blacklistedProviders
    /**
     * All providers loaded from the contains
     */
    val providers: Set<Provider> get() = _providers
    /**
     * All Providers that are not blacklisted
     */
    val availableProviders get() = providers.minus(blacklistedProviders)
    val providerRelationManager = ReactantCore.instance.reactantInstanceManager.getOrConstructWithoutInjection(ProviderRelationManager::class)
    /**
     * The last interpreted relations result by relation interpreters
     */
    var interpretedRelations: List<InterpretedProviderRelation> = listOf()

    /**
     * Available relation interpreters that will be used
     */
    val relationInterpreters = listOf(
            WrappedDynamicProviderRelationInterpreter(),
            SimpleInjectionComponentProviderRelationInterpreter(),
            NullableInjectionComponentProviderRelationInterpreter(),
            ArgumentInjectionComponentProviderRelationInterpreter()
    )


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
        interpretedRelations = availableProviders
                .flatMap { provider ->
                    relationInterpreters.mapNotNull { interpreter ->
                        interpreter.runCatching { interpret(provider, availableProviders) }
                                .onFailure { it.printStackTrace() }
                                .getOrNull()
                    }.flatten()
                }

        checkRelationFulfillRequirement(interpretedRelations,
                availableProviders.mapNotNull { it as? ComponentProvider<*> }
                        .flatMap { it.injectRequirements.map { requirement -> requirement to it } }
                        .toMap());

        interpretedRelations.forEach { relation ->
            if (relation.interpretTarget is ComponentProvider<*>) {
                relation.resolvedRequirements.forEach {
                    relation.interpretTarget.resolvedRequirements[it.first] = it.second
                }
            }
            providerRelationManager.addDependencyRelation(relation.interpretTarget, setOf(relation.dependOn))
        }
    }

    private fun checkRelationFulfillRequirement(relations: List<InterpretedProviderRelation>, requirementProviderMap: Map<InjectRequirement, Provider>) {
        val requirementRelationMap = relations
                .flatMap { relation -> relation.resolvedRequirements.map { it.first }.map { requirement -> requirement to relation } }
                .groupBy { it.first };
        val notFulfilledRequirements = availableProviders.mapNotNull { it as? ComponentProvider<*> }
                .filter { it.catchedThrowable == null }
                .flatMap { it.injectRequirements }
                .filter { !requirementRelationMap.containsKey(it) }

        val msg = notFulfilledRequirements
                .map { notFulfilledRequirement -> requirementProviderMap[notFulfilledRequirement]!!.productType }
                .joinToString(",") { "{$it}" }


        assert(notFulfilledRequirements.isNotEmpty())
    }
}
