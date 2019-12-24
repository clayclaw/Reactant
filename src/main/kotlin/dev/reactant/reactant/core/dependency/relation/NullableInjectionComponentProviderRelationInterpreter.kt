package dev.reactant.reactant.core.dependency.relation

import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import dev.reactant.reactant.core.dependency.injection.producer.NullProvider
import dev.reactant.reactant.core.dependency.injection.producer.Provider

/**
 * Similar to SimpleInjectionComponentProviderRelatuionInterpreter, but return NullProvider when the requirment cannot be resolved
 */
class NullableInjectionComponentProviderRelationInterpreter : SimpleInjectionComponentProviderRelationInterpreter() {
    override fun isRequirementInterpretable(requirement: InjectRequirement): Boolean = requirement.requiredType.run { isMarkedNullable && arguments.isEmpty() }

    override fun solve(interpretTarget: Provider, providers: Set<Provider>, injectRequirement: InjectRequirement): Provider =
            SimpleInjectionResolverUtil.solve(interpretTarget, providers, injectRequirement)
                    ?: NullProvider
}
