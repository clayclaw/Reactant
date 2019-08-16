package dev.reactant.reactant.service.spec.parser

import io.reactivex.Single
import kotlin.reflect.KClass

interface ParserService {
    /**
     * Encode your object to target format
     */
    fun encode(obj: Any): Single<String>

    /**
     * Parse the encoded string to object with model class
     */
    fun <T : Any> decode(modelClass: KClass<T>, encoded: String): Single<T>
}
