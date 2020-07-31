package dev.reactant.reactant.extra.config

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.config.ConfigService
import dev.reactant.reactant.service.spec.file.text.TextFileReaderService
import dev.reactant.reactant.service.spec.file.text.TextFileWriterService
import dev.reactant.reactant.service.spec.parser.ParserService
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.io.FileNotFoundException
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Component
class ReactantConfigService(
        private val fileReader: TextFileReaderService,
        private val fileWriter: TextFileWriterService
) : ConfigService {

    private fun <T : Any> get(parser: ParserService, rawResult: Single<T>, path: String, modelType: KType? = null): Maybe<Config<T>> = rawResult
            .toMaybe()
            .onErrorResumeNext { e ->
                when (e) {
                    is FileNotFoundException -> Maybe.empty()
                    else -> Maybe.error(e)
                }
            }.map { content -> ConfigImpl(path, content, parser, modelType) }

    override fun <T : Any> get(parser: ParserService, modelClass: KClass<T>, path: String): Maybe<Config<T>> =
            loadContent(parser, modelClass, path).let { get(parser, it, path) }

    override fun <T : Any> get(parser: ParserService, modelType: KType, path: String): Maybe<Config<T>> =
            loadContent<T>(parser, modelType, path).let { get(parser, it, path, modelType) }

    override fun <T : Any> getOrDefault(parser: ParserService, modelClass: KClass<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>> =
            Single.fromCallable { File(path) }
                    .flatMapMaybe { get(parser, modelClass, path) }
                    .switchIfEmpty(Single.fromCallable { ConfigImpl(path, defaultContentCallable(), parser) })

    override fun <T : Any> getOrDefault(parser: ParserService, modelType: KType, path: String, defaultContentCallable: () -> T): Single<Config<T>> =
            Single.fromCallable { File(path) }
                    .flatMapMaybe { get<T>(parser, modelType, path) }
                    .switchIfEmpty(Single.fromCallable { ConfigImpl(path, defaultContentCallable(), parser, modelType) })

    override fun <T : Any> getOrPut(parser: ParserService, modelClass: KClass<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>> =
            getOrDefault(parser, modelClass, path, defaultContentCallable)
                    .doOnSuccess { it.save().blockingAwait() }

    override fun <T : Any> getOrPut(parser: ParserService, modelType: KType, path: String, defaultContentCallable: () -> T): Single<Config<T>> =
            getOrDefault(parser, modelType, path, defaultContentCallable)
                    .doOnSuccess { it.save().blockingAwait() }

    override fun <T : Any> remove(config: Config<T>): Completable =
            Completable.fromCallable {
                File(config.path).let {
                    when {
                        !it.exists() -> throw FileNotFoundException("File not exist")
                        !it.isFile -> throw IllegalArgumentException("Not a file")
                        else -> it.delete()
                    }
                }
            }

    override fun <T : Any> save(config: Config<T>): Completable {
        if (config !is ConfigImpl) throw UnsupportedOperationException("Unmatched config type")
        val parseResult =
                if (config.modelType != null) config.parser.encode(config.content, config.modelType!!)
                else config.parser.encode(config.content)
        return parseResult.flatMapCompletable { encoded -> fileWriter.write(File(config.path), encoded) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> refresh(config: Config<T>): Completable {
        if (config !is ConfigImpl<*>) throw IllegalArgumentException("Not a correct config")
        val loadResult =
                if (config.modelType != null) loadContent(config.parser, config.modelType!!, config.path)
                else loadContent(config.parser, config.content::class as KClass<T>, config.path)
        return loadResult
                .doOnSuccess { content -> (config as ConfigImpl).content = content }
                .ignoreElement()
    }

    protected fun <T : Any> loadContent(parserService: ParserService, modelType: KType, path: String): Single<T> {
        return fileReader.readAll(File(path))
                .map { lines -> lines.joinToString("\n") }
                .flatMap { raw -> parserService.decode<T>(raw, modelType) }
    }

    protected fun <T : Any> loadContent(parserService: ParserService, modelClass: KClass<T>, path: String): Single<T> {
        return fileReader.readAll(File(path))
                .map { lines -> lines.joinToString("\n") }
                .flatMap { raw -> parserService.decode(raw, modelClass) }
    }

    protected inner class ConfigImpl<T : Any>(
            override val path: String,
            override var content: T,
            override val parser: ParserService,
            override val modelType: KType? = null) : Config<T> {

        override fun save(): Completable = this@ReactantConfigService.save(this)

        override fun refresh(): Completable = this@ReactantConfigService.refresh(this)
        override fun remove(): Completable = this@ReactantConfigService.remove(this)
    }
}
