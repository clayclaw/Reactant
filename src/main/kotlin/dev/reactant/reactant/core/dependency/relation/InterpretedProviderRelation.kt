package dev.reactant.reactant.core.dependency.relation

import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import dev.reactant.reactant.core.dependency.injection.producer.Provider

class InterpretedProviderRelation(
        val interpretBy: ProviderRelationInterpreter,
        val interpretTarget: Provider,
        val dependOn: Provider,
        val reason: String,
        val resolvedRequirements: Set<Pair<InjectRequirement, Provider>> = setOf()
) {
    override fun toString(): String {
        return "{ interpretedBy: ${interpretBy.javaClass.canonicalName}, provider: ${interpretTarget.productType}, dependOn: ${dependOn.productType}," +
                " reason: ${reason}, resolvedRequirements: ${resolvedRequirements.map { it.first.toString() }.joinToString(",")} }"
    }
}
