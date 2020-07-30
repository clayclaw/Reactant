package dev.reactant.reactant.extra.config

import dev.reactant.reactant.extra.parser.GsonJsonParserService
import dev.reactant.reactant.extra.parser.SnakeYamlParserService
import dev.reactant.reactant.extra.parser.Toml4jTomlParserService

open class ConfigParserDecider(
        private val jsonParserService: GsonJsonParserService,
        private val yamlParserService: SnakeYamlParserService,
        private val tomlParserService: Toml4jTomlParserService
) {

    fun getParserByPath(path: String) = when {
        path.matches("^.*\\.ya?ml$".toRegex()) -> yamlParserService
        path.matches("^.*\\.json$".toRegex()) -> jsonParserService
        path.matches("^.*\\.toml$".toRegex()) -> tomlParserService
        else -> throw IllegalStateException()
    }
}
