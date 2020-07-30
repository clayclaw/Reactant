package dev.reactant.reactant.service.spec.config

import dev.reactant.reactant.service.spec.parser.ParserService
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface ConfigService {

    /**
     * Save the content.
     *
     * Create if the file not exist
     */
    fun <T : Any> save(config: Config<T>): Completable

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
     * Provide additional generic type info with KType instead of KClass, useful for some parser like GSON
     * @see get
     */
    fun <T : Any> get(parser: ParserService, modelType: KType, path: String): Maybe<Config<T>>

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
     * Provide additional generic type info with KType instead of KClass, useful for some parser like GSON
     * @see getOrDefault
     */
    fun <T : Any> getOrDefault(parser: ParserService, modelType: KType, path: String, defaultContentCallable: () -> T): Single<Config<T>>

    /**
     * Load a config or save and return a default value when file not exist
     */
    fun <T : Any> getOrPut(parser: ParserService, modelClass: KClass<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>>

    /**
     * Provide additional generic type info with KType instead of KClass, useful for some parser like GSON
     * @see getOrPut
     */
    fun <T : Any> getOrPut(parser: ParserService, modelType: KType, path: String, defaultContentCallable: () -> T): Single<Config<T>>

    /**
     * Delete a config
     * Error will be observed if the file is not able to delete
     * [java.io.FileNotFoundException]: File not exist
     * [IllegalArgumentException]: Not a file
     */
    fun <T : Any> remove(config: Config<T>): Completable

    /**
     * Load the config and replace the content, the content should be a new object.
     */
    fun <T : Any> refresh(config: Config<T>): Completable

}

/**
 * Reified version
 * @see ConfigService.get
 *
 * NOT support KType passing, reason: https://youtrack.jetbrains.com/issue/KT-28230
 */
inline fun <reified T : Any> ConfigService.get(parser: ParserService, path: String) = get(parser, T::class, path)

/**
 * Reified version
 * @see ConfigService.getOrDefault
 *
 * NOT support KType passing, reason: https://youtrack.jetbrains.com/issue/KT-28230
 */
inline fun <reified T : Any> ConfigService.getOrDefault(parser: ParserService, path: String, noinline defaultContentCallable: () -> T) = getOrDefault(parser, T::class, path, defaultContentCallable)
