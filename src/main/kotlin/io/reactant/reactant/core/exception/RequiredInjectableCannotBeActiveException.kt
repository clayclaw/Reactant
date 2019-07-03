package io.reactant.reactant.core.exception

import io.reactant.reactant.core.dependency.injection.producer.Provider

class RequiredInjectableCannotBeActiveException(requester: Provider, cause: Throwable) : Exception(cause)
