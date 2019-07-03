package io.reactant.reactant.core.exception

import io.reactant.reactant.core.dependency.injection.producer.Provider

class CyclicDependencyRelationException(val dependencies: List<Provider>) : Exception()
