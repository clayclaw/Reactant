package net.swamphut.swampium.service.spec.config

import io.reactivex.Completable
import io.reactivex.Single
import net.swamphut.swampium.service.spec.parser.ParserService

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
    fun <T : Any> load(parser: ParserService, modelClass: Class<T>, path: String): Single<Config<T>>

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
    fun <T : Any> loadOrDefault(parser: ParserService, modelClass: Class<T>, path: String, defaultContentCallable: () -> T): Single<Config<T>>

    /**
     * Load the config and replace the content, the content should be a new object.
     */
    fun refresh(config: Config<Any>): Completable

}
