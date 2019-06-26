package io.reactant.reactant.core.exception

import io.reactant.reactant.core.dependency.injection.InjectRequirement
import io.reactant.reactant.core.dependency.injection.producer.InjectableWrapper

class InjectRequirementNotFulfilledException(val requester: InjectableWrapper,
                                             val requirements: Set<InjectRequirement>) : Exception()
