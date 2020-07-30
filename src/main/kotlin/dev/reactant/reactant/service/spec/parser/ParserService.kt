package dev.reactant.reactant.service.spec.parser

import io.reactivex.rxjava3.core.Single
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

interface ParserService {
    /**
     * Encode the object to target format
     */
    fun encode(obj: Any): Single<String>

    /**
     * Encode the object with type to target format
     */
    fun encode(obj: Any, modelType: KType): Single<String> = encode(obj)

    /**
     * Parse the encoded string to object with model class
     */
    @Deprecated("Confusing argument position", ReplaceWith("decode(encoded, modelClass)"))
    fun <T : Any> decode(modelClass: KClass<T>, encoded: String): Single<T> = decode(encoded, modelClass)

    /**
     * Parse the encoded string to object with model class
     */
    fun <T : Any> decode(encoded: String, modelClass: KClass<T>): Single<T>

    /**
     * Parse the encoded string to object with model type
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> decode(encoded: String, modelType: KType): Single<T> = decode(encoded, modelType.jvmErasure as KClass<T>)

}
