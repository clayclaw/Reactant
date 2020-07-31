package dev.reactant.reactant.extra.parser

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapterFactory
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.components.Components
import dev.reactant.reactant.core.dependency.layers.SystemLevel
import dev.reactant.reactant.extra.parser.gsonadapters.TypeAdapterPair
import dev.reactant.reactant.service.spec.parser.JsonParserService
import io.reactivex.rxjava3.core.Single
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType


@Component
open class GsonJsonParserService(
        private val typeAdapterFactories: Components<TypeAdapterFactory>,
        private val typeAdapters: Components<TypeAdapterPair>
) : JsonParserService, SystemLevel {
    protected val prettyGson = GsonBuilder()
            .also { builder -> typeAdapterFactories.forEach { builder.registerTypeAdapterFactory(it) } }
            .also { builder -> typeAdapters.forEach { builder.registerTypeAdapter(it.type, it.typeAdapter) } }
            .setPrettyPrinting().create()

    protected val gson = GsonBuilder()
            .also { builder -> typeAdapterFactories.forEach { builder.registerTypeAdapterFactory(it) } }
            .also { builder -> typeAdapters.forEach { builder.registerTypeAdapter(it.type, it.typeAdapter) } }
            .create()

    override fun encode(obj: Any, prettyPrint: Boolean): Single<String> =
            Single.defer { Single.just((if (prettyPrint) prettyGson else gson).toJson(obj)) }

    override fun encode(obj: Any, modelType: KType, prettyPrint: Boolean): Single<String> =
            Single.defer { Single.just((if (prettyPrint) prettyGson else gson).toJson(obj, modelType.javaType)) }

    override fun <T : Any> decode(encoded: String, modelClass: KClass<T>): Single<T> =
            Single.defer { Single.just(gson.fromJson(encoded, modelClass.java)) }

    override fun <T : Any> decode(encoded: String, modelType: KType): Single<T> =
            Single.defer { Single.just(gson.fromJson<T>(encoded, modelType.javaType)) }
}


