package net.swamphut.swampium.service.spec.parser

import io.reactivex.Single

interface ParserService {
    /**
     * Encode your object to target format
     */
    fun encode(obj: Any): Single<String>

    /**
     * Parse the encoded string to object with model class
     */
    fun <T> decode(modelClass: Class<T>, encoded: String): Single<T>
}
