package net.swamphut.swampium.utils

object PatternMatchingUtils {
    @JvmStatic
    fun matchWildcardOrRegex(pattern: String, test: String): Boolean {
        return if (pattern.startsWith('/') && pattern.endsWith('/') && pattern.length >= 2) {
            pattern.substring(1, pattern.length - 1).toRegex().matches(test)
        } else if (pattern.contains('*')) {
            matchWildcard(pattern, test)
        } else {
            pattern == test
        }
    }

    fun matchWildcard(wildcard: String, test: String): Boolean =
            "\\Q$wildcard\\E".replace("*", "\\E.*\\Q").toRegex().matches(test)
}
