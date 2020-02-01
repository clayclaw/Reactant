package dev.reactant.reactant.core.exception

import dev.reactant.reactant.core.dependency.injection.producer.Provider
import dev.reactant.reactant.core.dependency.relation.interpreters.ProviderRelationInterpreter

class ProviderRequirementCannotFulfilException(val reportedBy: ProviderRelationInterpreter,
                                               val provider: Provider, message: String) : RuntimeException("A interpreter has reported that a provider requirement cannot be fulfilled. \n" +
        String.format("%15s: %s\n", "reported by", reportedBy.javaClass.simpleName) +
        String.format("%15s: %s\n", "provider", provider.productType) +
        String.format("%15s: %s\n", "message", message)) {

}
