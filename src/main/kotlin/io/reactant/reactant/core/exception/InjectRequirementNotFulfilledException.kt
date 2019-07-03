package io.reactant.reactant.core.exception

import io.reactant.reactant.core.dependency.injection.InjectRequirement
import io.reactant.reactant.core.dependency.injection.producer.Provider

class InjectRequirementNotFulfilledException(val requester: Provider,
                                             val requirements: Set<InjectRequirement>) : Exception()
