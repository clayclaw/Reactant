package net.swamphut.swampium.service.spec.config

import io.reactivex.Completable
import net.swamphut.swampium.service.spec.parser.ParserService

/**
 * The container of the config entity
 *
 * @param <T> Model of config
</T> */
interface Config<out T : Any> {

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
}
