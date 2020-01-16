package dev.reactant.reactant.core.configs

import java.util.*

data class ServiceSpecifyingConfig(
        var specifyRules: List<ServiceSpecifyingRule> = ArrayList(),
        var blacklistRules: List<ServiceSpecifyingRule> = ArrayList()
) {
    inner class ServiceSpecifyingRule {
        var requester = ""
        var fulfillWith: List<ServiceProviderSpecifier> = ArrayList()
    }

    inner class ServiceProviderSpecifier {
        var require = ""
        var provider = ""
    }
}
