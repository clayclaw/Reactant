package dev.reactant.reactant.core.exception

import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.dependency.layers.SystemLevel
import dev.reactant.reactant.core.dependency.relation.InterpretedProviderRelation
import kotlin.reflect.full.starProjectedType

val star = "** System Level"
private fun displayProvider(provider: Provider): String {
    return "${provider.productType}  ${if (provider.canProvideType(SystemLevel::class.starProjectedType)) star else ""}"
}

class CyclicDependencyRelationException(val issuesOn: Provider, val dependencies: LinkedHashMap<Provider, InterpretedProviderRelation>)
    : Exception("Seem the snake is eating itself. It is probably not reactant's bug :C \n\n" +
        "   ${displayProvider(issuesOn)} " +
        dependencies.entries.reversed().map {
            """
        ↑ Require | reason: ${it.value.reason}
                  | resolved requirments: ${it.value.resolvedRequirements.map { it.first.requiredType }.joinToString(", ")}
                  | 
   ${displayProvider(it.key)} """
        }.joinToString("") + "\n")
