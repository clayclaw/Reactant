package net.swamphut.swampium.core.swobject.dependency.injectable

import kotlin.reflect.KType

/**
 * The producer info for resolve loading order
 */
interface InjectableProducerWrapper {
    val productType: KType;
    val producer: () -> Any
}
