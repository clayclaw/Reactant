package net.swamphut.swampium.core.dependency.injectable.producer

import kotlin.reflect.KType

/**
 * The producer info for resolve loading order
 */
interface InjectableProducerWrapper {
    val productType: KType;
    /**
     * Regex pattern to match name
     */
    val namePattern: String;
    val producer: (requestedType: KType, requestedName: String, requester: Any) -> Any
}
