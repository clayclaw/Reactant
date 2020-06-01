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

@Component
class ReactantConfigService(
        private val fileReader: TextFileReaderService,
        private val fileWriter: TextFileWriterService
) : ConfigService {

    override fun <T : Any> get(parser: ParserService, modelClass: KClass<T>, path: String): Maybe<Config<T>> =
            loadContent(parser, modelClass, path)
                    .toMaybe()
                    .onErrorResumeNext { e ->
                        when (e) {
                            is FileNotFoundException -> Maybe.empty()
                            else -> Maybe.error(e)
                        }
                    }
                    .map { content -> ConfigImpl(path, content, parser) }

    override fun <T : Any> getOrDefault(parser: ParserService, modelClass: KClass<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>> =
            Single.fromCallable { File(path) }
                    .flatMapMaybe { get(parser, modelClass, path) }
                    .switchIfEmpty(Single.fromCallable { ConfigImpl(path, defaultContentCallable(), parser) })

    override fun <T : Any> getOrPut(parser: ParserService, modelClass: KClass<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>> =
            getOrDefault(parser, modelClass, path, defaultContentCallable)
                    .doOnSuccess { it.save().blockingAwait() }

    override fun remove(config: Config<Any>): Completable =
            Completable.fromCallable {
                File(config.path).let {
                    when {
                        !it.exists() -> throw FileNotFoundException("File not exist")
                        !it.isFile -> throw IllegalArgumentException("Not a file")
                        else -> it.delete()
                    }
                }
            }


    override fun save(config: Config<Any>): Completable {
        return config.parser.encode(config.content)
                .flatMapCompletable { encoded -> fileWriter.write(File(config.path), encoded) }
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
        override fun remove(): Completable = this@ReactantConfigService.remove(this)
    }
}
