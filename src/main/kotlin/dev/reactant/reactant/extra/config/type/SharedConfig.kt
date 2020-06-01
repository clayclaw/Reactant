package dev.reactant.reactant.extra.config.type

import dev.reactant.reactant.service.spec.parser.ParserService
import io.reactivex.rxjava3.core.Completable

interface SharedConfig<out T : Any> {
    val path: String

    val content: T

    val parser: ParserService

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
