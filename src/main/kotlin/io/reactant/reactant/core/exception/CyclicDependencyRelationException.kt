package io.reactant.reactant.core.exception

import io.reactant.reactant.core.dependency.injection.producer.InjectableWrapper

class CyclicDependencyRelationException(val dependencies: List<InjectableWrapper>) : Exception()
