package dev.reactant.reactant.core.exception

import dev.reactant.reactant.core.dependency.injection.producer.Provider

class RequiredInjectableCannotBeActiveException(requester: Provider, cause: Throwable) : Exception(cause)
