package dev.reactant.reactant.service.spec.config

import dev.reactant.reactant.service.spec.parser.ParserService
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import kotlin.reflect.KClass

interface ConfigService {

    /**
     * Save the content.
     *
     * Create if the file not exist
     */
    fun save(config: Config<Any>): Completable

    /**
     * Load a config
     *
     * Error will be observed if the file is not able to read:
     * [java.io.FileNotFoundException]: File not exist
     * [IllegalArgumentException]: Not a file
     *
     */
    fun <T : Any> get(parser: ParserService, modelClass: KClass<T>, path: String): Maybe<Config<T>>

    /**
     * Load a config or return a default value when file not exist
     *
     * Error will be observed if the file is not able to read:
     * [IllegalArgumentException]: Not a file
     *
     * @param defaultContentCallable the callable which return the default value,
     * will only be called while config missing
     */
    fun <T : Any> getOrDefault(parser: ParserService, modelClass: KClass<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>>

    /**
     * Load a config or save and return a default value when file not exist
     */
    fun <T : Any> getOrPut(parser: ParserService, modelClass: KClass<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>>

    /**
     * Delete a config
     * Error will be observed if the file is not able to delete
     * [java.io.FileNotFoundException]: File not exist
     * [IllegalArgumentException]: Not a file
     */
    fun remove(config: Config<Any>): Completable

    /**
     * Load the config and replace the content, the content should be a new object.
     */
    fun refresh(config: Config<Any>): Completable

}

inline fun <reified T : Any> ConfigService.get(parser: ParserService, path: String) = get(parser, T::class, path)
inline fun <reified T : Any> ConfigService.getOrDefault(parser: ParserService, path: String, noinline defaultContentCallable: () -> T) = getOrDefault(parser, T::class, path, defaultContentCallable)
