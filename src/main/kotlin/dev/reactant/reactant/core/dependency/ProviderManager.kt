package dev.reactant.reactant.core.dependency

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.dependency.relation.*
import dev.reactant.reactant.core.exception.ProviderRequirementCannotFulfilException

@Component
class ProviderManager {
    private val _providers = HashSet<Provider>()
    private val _blacklistedProviders = HashSet<Provider>()
    val blacklistedProviders get() = _blacklistedProviders
    val providers: Set<Provider> get() = _providers
    val providerRelationManager = ReactantCore.instance.reactantInstanceManager.getOrConstructWithoutInjection(ProviderRelationManager::class)
    var interpretedRelations: List<InterpretedProviderRelation> = listOf()

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

        val relationInterpreters = listOf(
                WrappedDynamicProviderRelationInterpreter(),
                SimpleInjectionComponentProviderRelationInterpreter(),
                NullableInjectionComponentProviderRelationInterpreter(),
                ArgumentInjectionComponentProviderRelationInterpreter()
        )

        interpretedRelations = _providers
                .flatMap { provider ->
                    relationInterpreters.mapNotNull { interpreter ->
                        try {
                            interpreter.interpret(provider, providers)
                        } catch (e: ProviderRequirementCannotFulfilException) {
                            e.printStackTrace()
                            null
                        }
                    }.flatten()
                }

        checkRelationFulfillRequirement(interpretedRelations,
                _providers.mapNotNull { it as? ComponentProvider<*> }
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
        val notFulfilledRequirements = _providers.mapNotNull { it as? ComponentProvider<*> }
                .filter { it.catchedThrowable == null }
                .flatMap { it.injectRequirements }
                .filter { !requirementRelationMap.containsKey(it) }

        val msg = notFulfilledRequirements
                .map { notFulfilledRequirement -> requirementProviderMap[notFulfilledRequirement]!!.productType }
                .joinToString(",") { "{$it}" }


        if (notFulfilledRequirements.isNotEmpty()) {
            throw IllegalStateException("There have some requirements which are not handled by the interpreter, " +
                    "it is typically the bug of Reactant component relation resolver or you have used some unsupported features. " +
                    "Please report to Reactant Dev with the related source file. not fulfilled providers: [$msg]")
        }
    }
}
