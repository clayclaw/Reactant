package dev.reactant.reactant.core.dependency

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.producer.ComponentProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.dependency.layers.FunctionalityLayer
import dev.reactant.reactant.core.dependency.layers.SystemLevel
import dev.reactant.reactant.core.dependency.relation.InterpretedProviderRelation
import dev.reactant.reactant.core.dependency.relation.interpreters.ArgumentInjectionComponentProviderRelationInterpreter
import dev.reactant.reactant.core.dependency.relation.interpreters.NullableInjectionRelationInterpreter
import dev.reactant.reactant.core.dependency.relation.interpreters.WrappedDynamicProviderRelationInterpreter
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

@Component
class ProviderManager : SystemLevel {
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
            ArgumentInjectionComponentProviderRelationInterpreter(),
            NullableInjectionRelationInterpreter()
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

        if(availableProviders.isEmpty()) {
            ReactantCore.logger.warn("No available provider is found! Cancelled relation decision.")
            return
        }

        interpretedRelations = availableProviders
                .flatMap { provider ->
                    relationInterpreters.mapNotNull { interpreter ->
                        interpreter.runCatching { interpret(provider, availableProviders) }
                                .onFailure { it.printStackTrace() }
                                .getOrNull()
                    }.flatten()
                }

        val requirementSolvingRelation = interpretedRelations
                .flatMap { relation -> relation.resolvedRequirements.map { it to relation } }
                .groupBy { it.first.first }
                .mapValues { it.value.map { it.second }.first() } // take the first priority relation, and abandon other

        val directRelation = interpretedRelations.filter { it.directRelation }

        requirementSolvingRelation.forEach { (_, relation) ->
            if (relation.interpretTarget is ComponentProvider<*>) {
                relation.resolvedRequirements.forEach {
                    relation.interpretTarget.resolvedRequirements[it.first] = it.second
                }
            }
            providerRelationManager.addDependencyRelation(relation)
        }

        requirementSolvingRelation.values.union(directRelation).forEach { relation ->
            providerRelationManager.addDependencyRelation(relation)
        }

        // Make all non-system level component provider depends on FunctionalityLayer
        val functionalityLayerProvider = availableProviders.find {
            it.productType.jvmErasure == FunctionalityLayer::class
        }!!
        availableProviders
                .filter { it is ComponentProvider<*> }
                .filter { it != functionalityLayerProvider && !it.canProvideType(SystemLevel::class.starProjectedType) }
                .forEach {
                    providerRelationManager.addDependencyRelation(InterpretedProviderRelation(
                            this, it, functionalityLayerProvider,
                            "All component not implemented SystemLevel is consider as Functionality Layer"
                    ))
                }

    }
}
