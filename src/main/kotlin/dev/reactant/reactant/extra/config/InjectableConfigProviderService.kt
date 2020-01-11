package dev.reactant.reactant.extra.config

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Provide
import dev.reactant.reactant.extra.config.type.SharedConfig
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.config.ConfigService
import dev.reactant.reactant.service.spec.parser.JsonParserService
import dev.reactant.reactant.service.spec.parser.ParserService
import dev.reactant.reactant.service.spec.parser.TomlParserService
import dev.reactant.reactant.service.spec.parser.YamlParserService
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

@Component
private class InjectableConfigProviderService(
        private val jsonParserService: JsonParserService,
        private val yamlParserService: YamlParserService,
        private val tomlParserService: TomlParserService,
        private val configService: ConfigService
) {
    private val configParserDecider = ConfigParserDecider(jsonParserService, yamlParserService, tomlParserService)
    private val sharedConfigs = HashMap<Pair<KType, String>, DelegatedSharedConfig>()

    @Provide("^.*\\.(ya?ml|json|toml)$", true)
    private fun provideConfig(kType: KType, name: String): Config<Any> = getConfig(configParserDecider.getParserByPath(name), kType, name)


    @Provide("^.*\\.(ya?ml|json|toml)$", true)
    private fun provideSharedConfig(kType: KType, name: String): SharedConfig<Any> =
            sharedConfigs.getOrPut(kType to name) {
                DelegatedSharedConfig(getConfig(configParserDecider.getParserByPath(name), kType, name))
            }

    private class DelegatedSharedConfig(val config: Config<Any>) : SharedConfig<Any>, Config<Any> by config

    private fun getConfig(parser: ParserService, kType: KType, path: String): Config<Any> {
        @Suppress("UNCHECKED_CAST")
        val configClass = kType.arguments.first().type!!.jvmErasure as KClass<Any>

        return configService.getOrPut(parser, configClass, path) {
            configClass.java.getDeclaredConstructor().newInstance()
        }.blockingGet()
    }
}
