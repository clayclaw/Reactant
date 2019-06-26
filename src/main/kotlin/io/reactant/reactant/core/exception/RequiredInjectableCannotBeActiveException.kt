package io.reactant.reactant.core.exception

import io.reactant.reactant.core.dependency.injection.producer.InjectableWrapper

class RequiredInjectableCannotBeActiveException(requester: InjectableWrapper, cause: Throwable) : Exception(cause)
