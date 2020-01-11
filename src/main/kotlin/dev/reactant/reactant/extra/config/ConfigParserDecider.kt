package dev.reactant.reactant.extra.config

import dev.reactant.reactant.service.spec.parser.JsonParserService
import dev.reactant.reactant.service.spec.parser.TomlParserService
import dev.reactant.reactant.service.spec.parser.YamlParserService

open class ConfigParserDecider(
        private val jsonParserService: JsonParserService,
        private val yamlParserService: YamlParserService,
        private val tomlParserService: TomlParserService
) {

    fun getParserByPath(path: String) = when {
        path.matches("^.*\\.ya?ml$".toRegex()) -> yamlParserService
        path.matches("^.*\\.json$".toRegex()) -> jsonParserService
        path.matches("^.*\\.toml$".toRegex()) -> tomlParserService
        else -> throw IllegalStateException()
    }
}
