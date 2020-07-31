package dev.reactant.reactant.extra.parser

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapterFactory
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.components.Components
import dev.reactant.reactant.service.spec.parser.JsonParserService
import io.reactivex.rxjava3.core.Single
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType


interface TypeAdapterPair {
    val type: Type;

    /**
     * Type adapter that will register into GSON
     * @see GsonBuilder.registerTypeAdapter
     */
    val typeAdapter: Any
}

@Component
open class GsonJsonParserService(
        val typeAdapterFactories: Components<TypeAdapterFactory>,
        val typeAdapters: Components<TypeAdapterPair>
) : JsonParserService {
    protected val gson = GsonBuilder()
            .also { builder -> typeAdapterFactories.forEach { builder.registerTypeAdapterFactory(it) } }
            .also { builder -> typeAdapters.forEach { builder.registerTypeAdapter(it.type, it.typeAdapter) } }
            .setPrettyPrinting().create()!!

    override fun encode(obj: Any): Single<String> =
            Single.defer { Single.just(gson.toJson(obj)) }

    override fun encode(obj: Any, modelType: KType): Single<String> =
            Single.defer { Single.just(gson.toJson(obj, modelType.javaType)) }

    override fun <T : Any> decode(encoded: String, modelClass: KClass<T>): Single<T> =
            Single.defer { Single.just(gson.fromJson(encoded, modelClass.java)) }

    override fun <T : Any> decode(encoded: String, modelType: KType): Single<T> =
            Single.defer { Single.just(gson.fromJson<T>(encoded, modelType.javaType)) }
}


