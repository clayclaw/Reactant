package dev.reactant.reactant.core.dependency.relation

import dev.reactant.reactant.core.dependency.injection.producer.Provider

interface ProviderRelationInterpreter {
    /**
     * Interpret the relation of the providers
     * @return Set of relation or null if the interpret target is not related to the interpreter
     */
    fun interpret(interpretTarget: Provider, providers: Set<Provider>): Set<InterpretedProviderRelation>?
}


