package dev.reactant.reactant.core.dependency.relation

import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import dev.reactant.reactant.core.dependency.injection.producer.NullProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider

/**
 * Similar to SimpleInjectionComponentProviderRelatuionInterpreter, but return NullProvider when the requirement cannot be resolved
 */
class NullableInjectionRelationInterpreter : SimpleInjectionComponentProviderRelationInterpreter() {
    override fun isRequirementInterpretable(requirement: InjectRequirement): Boolean = requirement.requiredType.run { isMarkedNullable }

    override fun solve(interpretTarget: Provider, providers: Set<Provider>, injectRequirement: InjectRequirement): Pair<Provider, Int> = NullProvider to Int.MAX_VALUE
}
