package dev.reactant.reactant.extra.config

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.config.ConfigService
import dev.reactant.reactant.service.spec.file.text.TextFileReaderService
import dev.reactant.reactant.service.spec.file.text.TextFileWriterService
import dev.reactant.reactant.service.spec.parser.ParserService
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import kotlin.reflect.KClass

@Component
class ReactantConfigService(
        private val fileReader: TextFileReaderService,
        private val fileWriter: TextFileWriterService
) : ConfigService {

    override fun <T : Any> load(parser: ParserService, modelClass: KClass<T>, path: String): Single<Config<T>> {
        return loadContent(parser, modelClass, path)
                .map { content -> ConfigImpl(path, content, parser) }
    }

    override fun <T : Any> loadOrDefault(parser: ParserService, modelClass: KClass<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>> {
        return Single.defer { Single.just(File(path)) }
                .flatMap { file ->
                    if (file.exists())
                        load(parser, modelClass, path)
                    else
                        Single.just(ConfigImpl(path, defaultContentCallable(), parser))
                }
    }


    override fun save(config: Config<Any>): Completable {
        return config.parser.encode(config.content)
                .flatMapCompletable { encoded -> fileWriter!!.write(File(config.path), encoded) }
    }

    override fun refresh(config: Config<Any>): Completable {
        if (config !is ConfigImpl<*>) throw IllegalArgumentException("Not a correct config")
        return loadContent(config.parser, config.content::class, config.path)
                .doOnSuccess { content -> (config as ConfigImpl).content = content }
                .ignoreElement()
    }

    protected fun <T : Any> loadContent(parserService: ParserService, modelClass: KClass<T>, path: String): Single<T> {
        return fileReader.readAll(File(path))
                .map { lines -> lines.joinToString("\n") }
                .flatMap { raw -> parserService.decode(modelClass, raw) }
    }

    protected inner class ConfigImpl<T : Any>(
            override val path: String,
            override var content: T,
            override val parser: ParserService) : Config<T> {

        override fun save(): Completable = this@ReactantConfigService.save(this)

        override fun refresh(): Completable = this@ReactantConfigService.refresh(this)
    }
}
