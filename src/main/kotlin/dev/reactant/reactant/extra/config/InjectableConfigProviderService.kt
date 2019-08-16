package dev.reactant.reactant.extra.config

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Provide
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.config.ConfigService
import dev.reactant.reactant.service.spec.parser.JsonParserService
import dev.reactant.reactant.service.spec.parser.ParserService
import dev.reactant.reactant.service.spec.parser.TomlParserService
import dev.reactant.reactant.service.spec.parser.YamlParserService
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

@Component
private class InjectableConfigProviderService(
        private val jsonParserService: JsonParserService,
        private val yamlParserService: YamlParserService,
        private val tomlParserService: TomlParserService,
        private val configService: ConfigService
) {
    @Provide("^.*\\.(ya?ml|json|toml)$", true)
    private fun provideConfig(kType: KType, name: String): Config<Any> {
        val parser = when {
            name.matches("^.*\\.ya?ml$".toRegex()) -> yamlParserService
            name.matches("^.*\\.json$".toRegex()) -> jsonParserService
            name.matches("^.*\\.toml$".toRegex()) -> tomlParserService
            else -> throw IllegalStateException()
        }
        return getConfig(parser, kType, name)
    }

    private fun getConfig(parser: ParserService, kType: KType, path: String): Config<Any> {
        @Suppress("UNCHECKED_CAST")
        val configClass = kType.arguments.first().type!!.jvmErasure as KClass<Any>
        var exist = true

        return configService.loadOrDefault(parser, configClass, path) {
            exist = false
            when {
                configClass.constructors.size > 1 ->
                    throw IllegalArgumentException("There have more than one constructor for config ${configClass.jvmName}")
                configClass.constructors.first().parameters.isNotEmpty() ->
                    throw IllegalArgumentException("Config constructor is not parameterless ${configClass.jvmName}")
                else -> return@loadOrDefault configClass.constructors.first().call()
            }
        }.blockingGet().also { if (!exist) it.save().blockingAwait() }
    }
}
