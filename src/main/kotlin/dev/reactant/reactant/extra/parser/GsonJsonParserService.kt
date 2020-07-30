package dev.reactant.reactant.extra.parser

import com.google.gson.GsonBuilder
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.service.spec.parser.JsonParserService
import io.reactivex.rxjava3.core.Single
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

@Component
open class GsonJsonParserService : JsonParserService {
    protected val gson = GsonBuilder().setPrettyPrinting().create()!!

    override fun encode(obj: Any): Single<String> =
            Single.defer { Single.just(gson.toJson(obj)) }

    override fun encode(obj: Any, modelType: KType): Single<String> =
            Single.defer { Single.just(gson.toJson(obj, modelType.javaType)) }

    override fun <T : Any> decode(encoded: String, modelClass: KClass<T>): Single<T> =
            Single.defer { Single.just(gson.fromJson(encoded, modelClass.java)) }

    override fun <T : Any> decode(encoded: String, modelType: KType): Single<T> =
            Single.defer { Single.just(gson.fromJson<T>(encoded, modelType.javaType)) }
}
