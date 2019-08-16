package dev.reactant.reactant.service.spec.config

import dev.reactant.reactant.service.spec.parser.ParserService
import io.reactivex.Completable
import io.reactivex.Single
import kotlin.reflect.KClass

interface ConfigService {

    /**
     * Save the content.
     *
     *
     * Create if the file not exist
     */
    fun save(config: Config<Any>): Completable

    /**
     * Load a config
     *
     *
     * Error will be observed if the file is not able to read:
     *
     *  * [java.io.FileNotFoundException]: File not exist
     *  * [IllegalArgumentException]: Not a file
     *
     */
    fun <T : Any> load(parser: ParserService, modelClass: KClass<T>, path: String): Single<Config<T>>

    /**
     * Load a config or return a default value when file not exist
     *
     *
     * Error will be observed if the file is not able to read:
     *
     *  * [IllegalArgumentException]: Not a file
     *
     *
     * @param defaultContentCallable the callable which return the default value,
     * will only be called while config missing
     */
    fun <T : Any> loadOrDefault(parser: ParserService, modelClass: KClass<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>>

    /**
     * Load the config and replace the content, the content should be a new object.
     */
    fun refresh(config: Config<Any>): Completable

}

inline fun <reified T : Any> ConfigService.load(parser: ParserService, path: String) = load(parser, T::class, path)
inline fun <reified T : Any> ConfigService.loadOrDefault(parser: ParserService, path: String, noinline defaultContentCallable: () -> T) = loadOrDefault(parser, T::class, path, defaultContentCallable)
