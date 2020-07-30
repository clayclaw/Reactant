package dev.reactant.reactant.service.spec.config

import dev.reactant.reactant.service.spec.parser.ParserService
import io.reactivex.rxjava3.core.Completable
import kotlin.reflect.KType

/**
 * The container of the config entity
 *
 * @param <T> Model of config
</T> */
interface Config<T : Any> {

    val path: String

    var content: T

    val parser: ParserService

    val modelType: KType?

    /**
     * Save the changes of config
     */
    fun save(): Completable

    /**
     * Abandon unsaved changes and load newest version from storage
     */
    fun refresh(): Completable

    fun remove(): Completable
}
