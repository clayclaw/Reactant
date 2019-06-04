package net.swamphut.swampium.extra.config

import io.reactivex.Completable
import io.reactivex.Single
import net.swamphut.swampium.core.dependency.injection.Inject
import net.swamphut.swampium.core.dependency.provide.ServiceProvider
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.service.spec.config.Config
import net.swamphut.swampium.service.spec.config.ConfigService
import net.swamphut.swampium.service.spec.file.text.TextFileReaderService
import net.swamphut.swampium.service.spec.file.text.TextFileWriterService
import net.swamphut.swampium.service.spec.parser.ParserService
import java.io.File

@SwObject
@ServiceProvider(provide = [ConfigService::class])
class SwampiumConfigService : ConfigService {
    @Inject
    private val fileReader: TextFileReaderService? = null
    @Inject
    private val fileWriter: TextFileWriterService? = null

    override fun <T : Any> load(parser: ParserService, modelClass: Class<T>, path: String): Single<Config<T>> {
        return loadContent(parser, modelClass, path)
                .map { content -> ConfigImpl(path, content, parser) }
    }

    override fun <T : Any> loadOrDefault(parser: ParserService, modelClass: Class<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>> {
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
        return loadContent(config.parser, config.content.javaClass, config.path)
                .doOnSuccess { content -> (config as ConfigImpl).content = content }
                .ignoreElement()
    }

    protected fun <T> loadContent(parserService: ParserService, modelClass: Class<T>, path: String): Single<T> {
        return fileReader!!.readAll(File(path))
                .map { lines -> lines.joinToString("\n") }
                .flatMap { raw -> parserService.decode(modelClass, raw) }
    }

    protected inner class ConfigImpl<T : Any>(
            override val path: String,
            override var content: T,
            override val parser: ParserService) : Config<T> {

        override fun save(): Completable = this@SwampiumConfigService.save(this)

        override fun refresh(): Completable = this@SwampiumConfigService.refresh(this)
    }
}
