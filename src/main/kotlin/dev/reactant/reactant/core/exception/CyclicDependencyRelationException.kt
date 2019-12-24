package dev.reactant.reactant.core.exception

import dev.reactant.reactant.core.dependency.injection.producer.Provider

class CyclicDependencyRelationException(val dependencies: List<Provider>)
    : Exception("Seem the snake is eating itself. It is probably not reactant's bug :C \n" +
        dependencies.map { "   ${it.productType}" }.joinToString("\n        ↑ Require\n"))
