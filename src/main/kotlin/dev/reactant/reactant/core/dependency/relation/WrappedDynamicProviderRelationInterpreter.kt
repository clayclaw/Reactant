package dev.reactant.reactant.core.dependency.relation

import dev.reactant.reactant.core.dependency.injection.producer.DynamicProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.exception.ProviderRequirementCannotFulfilException

/**
 * Since the dynamic provider is wrapped by a parent component
 * So they have the direct relation
 */
class WrappedDynamicProviderRelationInterpreter : ProviderRelationInterpreter {
    override fun interpret(interpretTarget: Provider, providers: Set<Provider>): Set<InterpretedProviderRelation>? {
        if (interpretTarget !is DynamicProvider<*, *>) return null
        if (!providers.contains(interpretTarget.providedInWrapper)) {
            throw ProviderRequirementCannotFulfilException(this, interpretTarget,
                    "Impossible, a dynamic provider should be provided by a component, " +
                            "but the component cannot be find in the providers list")
        }
        return setOf(InterpretedProviderRelation(this, interpretTarget,
                interpretTarget.providedInWrapper,
                "It is a wrapper of the dynamic provider."))
    }
}
