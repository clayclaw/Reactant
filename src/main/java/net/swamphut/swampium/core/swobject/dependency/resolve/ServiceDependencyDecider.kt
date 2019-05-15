package net.swamphut.swampium.core.swobject.dependency.resolve

import net.swamphut.swampium.core.configs.ServiceSpecifyingConfig
import net.swamphut.swampium.core.swobject.dependency.ServiceProviderInfo
import net.swamphut.swampium.core.swobject.dependency.ServiceProviderManager
import net.swamphut.swampium.utils.PatternMatchingUtils

class ServiceDependencyDecider(val serviceSpecifyingConfig: ServiceSpecifyingConfig,
                               val serviceProviderManager: ServiceProviderManager) {
    val classDecidedDependencies = HashMap<Class<*>, HashMap<Class<*>, ServiceProviderInfo<Any>>>()
    fun <T : Any> getDecided(requester: Class<out Any>, requesting: Class<T>): ServiceProviderInfo<T>? {

        val decidedDependencies = classDecidedDependencies.getOrPut(requester, { HashMap() })

        // If already decided
        decidedDependencies.get(requesting)?.let { decided ->
            @Suppress("UNCHECKED_CAST")
            return decided as ServiceProviderInfo<T>
        }

        // If decidable
        decide(requester, requesting, serviceProviderManager.findPossibleProvider(requesting))
                .let {
                    if (it != null) {
                        decidedDependencies.put(requesting, it)
                        return it
                    }
                }

        // If not yet decidable
        return null
    }

    private fun <T : Any> decide(
            requester: Class<out Any>,
            requiring: Class<out T>,
            possibleProviders: Collection<ServiceProviderInfo<T>>
    ): ServiceProviderInfo<T>? {
        possibleProviders.filter { possibleProvider ->
            for (blacklistProviderPattern in serviceSpecifyingConfig.blacklistRules
                    .filter { rule -> PatternMatchingUtils.matchWildcardOrRegex(rule.requester, requester.canonicalName) }
                    .flatMap { rule -> rule.fullfillWith }
                    .filter { specifier -> PatternMatchingUtils.matchWildcardOrRegex(specifier.require, requiring.canonicalName) }
                    .map { specifier -> specifier.provider }
                    .reversed()) {
                if (PatternMatchingUtils.matchWildcardOrRegex(blacklistProviderPattern, possibleProvider.instance::class.java.canonicalName)) {
                    return@filter false
                }
            }
            return@filter true
        }.let { allowedPossibleProviders ->
            for (specifyProviderPattern in serviceSpecifyingConfig.specifyRules
                    .filter { rule -> PatternMatchingUtils.matchWildcardOrRegex(rule.requester, requester.canonicalName) }
                    .flatMap { rule -> rule.fullfillWith }
                    .filter { specifier -> PatternMatchingUtils.matchWildcardOrRegex(specifier.require, requiring.canonicalName) }
                    .map { specifier -> specifier.provider }
                    .reversed()) {
                for (possibleProvider in allowedPossibleProviders) {
                    if (PatternMatchingUtils.matchWildcardOrRegex(specifyProviderPattern, possibleProvider.instance::class.java.canonicalName)) {
                        return possibleProvider
                    }
                }
            }

            // If have at least one: use it
            // else: Not deciable yet
            return allowedPossibleProviders.firstOrNull()
        }
    }
}
