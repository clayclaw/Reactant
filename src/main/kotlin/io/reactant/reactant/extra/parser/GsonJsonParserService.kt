package io.reactant.reactant.extra.parser

import com.google.gson.GsonBuilder
import io.reactant.reactant.core.component.Component
import io.reactant.reactant.service.spec.parser.JsonParserService
import io.reactivex.Single
import kotlin.reflect.KClass

@Component
class GsonJsonParserService : JsonParserService {
    private val gson = GsonBuilder().setPrettyPrinting().create()!!

    override fun encode(obj: Any): Single<String> =
            Single.defer { Single.just(gson.toJson(obj)) }

    override fun <T : Any> decode(modelClass: KClass<T>, encoded: String) =
            Single.defer { Single.just(gson.fromJson(encoded, modelClass.java)) }
}
