package net.swamphut.swampium.core.exception

import net.swamphut.swampium.core.dependency.injection.producer.InjectableWrapper

class RequiredInjectableCannotBeActiveException(requester: InjectableWrapper, cause: Throwable) : Exception(cause)
