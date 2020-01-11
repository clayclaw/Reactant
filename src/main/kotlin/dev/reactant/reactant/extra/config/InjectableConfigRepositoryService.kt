package dev.reactant.reactant.extra.config

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.ProviderManager
import dev.reactant.reactant.core.dependency.injection.Provide
import dev.reactant.reactant.extra.config.type.MultiConfigs
import dev.reactant.reactant.extra.config.type.SharedConfig
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.config.ConfigService
import dev.reactant.reactant.service.spec.parser.JsonParserService
import dev.reactant.reactant.service.spec.parser.TomlParserService
import dev.reactant.reactant.service.spec.parser.YamlParserService
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

@Component
private class InjectableConfigRepositoryService(
        private val jsonParserService: JsonParserService,
        private val yamlParserService: YamlParserService,
        private val tomlParserService: TomlParserService,
        private val configService: ConfigService,
        private val providerManager: ProviderManager
) {
    private val configParserDecider = ConfigParserDecider(jsonParserService, yamlParserService, tomlParserService)

    private class DelegatedSharedConfig(val config: Config<Any>) : SharedConfig<Any>, Config<Any> by config

    @Provide(".*", true)
    private fun getConfigRepository(kType: KType, path: String): MultiConfigs<Any> {
        @Suppress("UNCHECKED_CAST")
        val configClass = kType.arguments.first().type!!.jvmErasure as KClass<Any>
        return MultiConfigsImpl(File(path), configClass, configParserDecider)
    }

    private inner class MultiConfigsImpl<T : Any>(
            override val configFolder: File, override val modelClass: KClass<T>, val configParserDecider: ConfigParserDecider
    ) : MultiConfigs<T> {
        init {
            when {
                !configFolder.exists() -> configFolder.mkdirs()
                !configFolder.isDirectory -> throw IllegalArgumentException("$configFolder is not a folder")
            }
        }

        override fun get(relativePath: String): Maybe<Config<T>> =
                configService.get(configParserDecider.getParserByPath(relativePath), modelClass, "${configFolder.absolutePath}/$relativePath")

        override fun getOrDefault(relativePath: String, defaultContentCallable: () -> T): Single<Config<T>> =
                configService.getOrDefault(configParserDecider.getParserByPath(relativePath), modelClass, "${configFolder.absolutePath}/$relativePath", defaultContentCallable)

        override fun getOrPut(relativePath: String, defaultContentCallable: () -> T): Single<Config<T>> =
                configService.getOrPut(configParserDecider.getParserByPath(relativePath), modelClass, "${configFolder.absolutePath}/$relativePath", defaultContentCallable)

        override fun getAll(recursively: Boolean): Observable<Config<T>> =
                Observable.defer { configFolder.walkTopDown().filter { it.isFile }.toObservable() }
                        .flatMapSingle {
                            configService.get(configParserDecider.getParserByPath(it.absolutePath), modelClass, it.absolutePath)
                                    .switchIfEmpty(Single.error<Config<T>> { IllegalStateException() })
                        }

    }
}
