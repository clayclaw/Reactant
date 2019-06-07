//package net.swamphut.swampium.core.dependency.resolve
//
//import net.swamphut.swampium.core.configs.ServiceSpecifyingConfig
//import net.swamphut.swampium.core.configs.ServiceSpecifyingConfig.ServiceSpecifyingRule
//import net.swamphut.swampium.utils.PatternMatchingUtils
//
//class ServiceDependencyDecider(private val serviceSpecifyingConfig: ServiceSpecifyingConfig,
//                               private val serviceProviderManager: ServiceProviderManager) {
//    private val classDecidedDependencies = HashMap<Class<*>, HashMap<Class<*>, ServiceProviderInfo<Any>>>()
//    fun <T : Any> getDecided(requester: Class<out Any>, requesting: Class<T>): ServiceProviderInfo<T>? {
//
//        val decidedDependencies = classDecidedDependencies.getOrPut(requester, { HashMap() })
//
//        // If already decided
//        decidedDependencies.get(requesting)?.let { decided ->
//            @Suppress("UNCHECKED_CAST")
//            return decided as ServiceProviderInfo<T>
//        }
//
//        // If decidable
//        decide(requester, requesting, serviceProviderManager.findPossibleProvider(requesting))
//                .let {
//                    if (it != null) {
//                        decidedDependencies.put(requesting, it)
//                        return it
//                    }
//                }
//
//        // If not yet decidable
//        return null
//    }
//
//    private fun <T : Any> decide(requester: Class<out Any>, requiring: Class<out T>,
//                                 possibleProviders: Collection<ServiceProviderInfo<T>>): ServiceProviderInfo<T>? {
//        val allowedPossibleProviders = possibleProviders.filter { !isProviderBlacklisted(requester, requiring, it) };
//        val firstMatchedSpecifiedProvider = getFirstMatchedSpecifiedProvider(requester, requiring, allowedPossibleProviders)
//        return firstMatchedSpecifiedProvider ?: allowedPossibleProviders.firstOrNull()
//    }
//
//    private fun <T : Any> getFirstMatchedSpecifiedProvider(requester: Class<out Any>, requiring: Class<out T>,
//                                                           allowedPossibleProviders: Collection<ServiceProviderInfo<T>>): ServiceProviderInfo<T>? {
//        getRulesSpecifiedProviderPatterns(requester, requiring, serviceSpecifyingConfig.specifyRules)
//                .mapNotNull { specifyProviderPattern ->
//                    allowedPossibleProviders.firstOrNull {
//                        PatternMatchingUtils.matchWildcardOrRegex(specifyProviderPattern, it.instanceClass.canonicalName)
//                    }
//                }
//                .firstOrNull()// first matched rule and first matched provider
//                .let { return it }
//    }
//
//
//    private fun <T : Any> isProviderBlacklisted(requester: Class<out Any>, requiring: Class<out T>,
//                                                filteringServiceProvider: ServiceProviderInfo<Any>): Boolean {
//        return getRulesSpecifiedProviderPatterns(requester, requiring, serviceSpecifyingConfig.blacklistRules)
//                .map { providerPattern ->
//                    PatternMatchingUtils.matchWildcardOrRegex(
//                            providerPattern, filteringServiceProvider.instanceClass.canonicalName)
//                }.fold(false) { sum, next -> sum || next } // if any true = all true
//    }
//
//    /**
//     * Get what providers are specified under the "requester and requiring" condition
//     */
//    private fun getRulesSpecifiedProviderPatterns(requester: Class<out Any>, requiring: Class<out Any>,
//                                                  ruleList: Collection<ServiceSpecifyingRule>): List<String> {
//        return ruleList.filter { rule -> PatternMatchingUtils.matchWildcardOrRegex(rule.requester, requester.canonicalName) }
//                .flatMap { rule -> rule.fullfillWith }
//                .filter { specifier -> PatternMatchingUtils.matchWildcardOrRegex(specifier.require, requiring.canonicalName) }
//                .map { specifier -> specifier.provider }
//                .reversed() // Match from bottom first
//    }
//}
