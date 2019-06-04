package net.swamphut.swampium.core.dependency.injectable

import net.swamphut.swampium.core.dependency.injectable.producer.InjectableProducerWrapper

interface Injectable {
    val producerWrapper: InjectableProducerWrapper

    /**
     * Get the existing or create a new instance (based on producer implementation) of the injectable
     */
    val getInstance():Any
}
