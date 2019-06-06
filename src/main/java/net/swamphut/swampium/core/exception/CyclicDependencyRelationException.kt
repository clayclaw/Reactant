package net.swamphut.swampium.core.exception

import net.swamphut.swampium.core.dependency.injectable.producer.InjectableProducerWrapper
import net.swamphut.swampium.core.dependency.injectable.producer.ProvideInjectableProducerWrapper

class CyclicDependencyRelationException(val dependencies: List<InjectableProducerWrapper>) : Exception()