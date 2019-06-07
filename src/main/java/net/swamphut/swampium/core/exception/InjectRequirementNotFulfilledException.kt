package net.swamphut.swampium.core.exception

import net.swamphut.swampium.core.dependency.injection.InjectRequirement
import net.swamphut.swampium.core.dependency.injection.producer.InjectableWrapper

class InjectRequirementNotFulfilledException(val requester: InjectableWrapper,
                                             val requirements: Set<InjectRequirement>) : Exception()
