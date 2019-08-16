package dev.reactant.reactant.core.exception

import dev.reactant.reactant.core.dependency.injection.producer.Provider

class CyclicDependencyRelationException(val dependencies: List<Provider>) : Exception()
