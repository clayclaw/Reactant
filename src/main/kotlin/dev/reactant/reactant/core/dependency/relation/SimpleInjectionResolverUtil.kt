package dev.reactant.reactant.core.dependency.relation

import dev.reactant.reactant.core.ReactantCore
import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import dev.reactant.reactant.core.dependency.injection.producer.Provider

object SimpleInjectionResolverUtil {
    fun solve(interpretTarget: Provider, providers: Set<Provider>, requirement: InjectRequirement): Provider? {
        val fulfillingDependencies = providers
                .filter { it.canProvideType(requirement.requiredType) } // type match
                .filter { it.namePattern.toRegex().matches(requirement.name) } // name match
        if (fulfillingDependencies.size > 1)

        //todo: configable
            ReactantCore.logger.error("There have more than one injectables providing for ${requirement.requiredType}(name: ${requirement.name})," +
                    " ${fulfillingDependencies.map { "${it.productType}(NamePattern:${it.namePattern})" }}")
        return fulfillingDependencies.firstOrNull()
    }
}
