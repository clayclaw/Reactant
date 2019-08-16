package dev.reactant.reactant.core.exception

import dev.reactant.reactant.core.dependency.injection.InjectRequirement
import dev.reactant.reactant.core.dependency.injection.producer.Provider

class InjectRequirementNotFulfilledException(val requester: Provider,
                                             val requirements: Set<InjectRequirement>) : Exception()
