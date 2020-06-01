package dev.reactant.reactant.extra.parser

import com.google.gson.GsonBuilder
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.service.spec.parser.JsonParserService
import io.reactivex.rxjava3.core.Single
import kotlin.reflect.KClass

@Component
class GsonJsonParserService : JsonParserService {
    private val gson = GsonBuilder().setPrettyPrinting().create()!!

    override fun encode(obj: Any): Single<String> =
            Single.defer { Single.just(gson.toJson(obj)) }

    override fun <T : Any> decode(modelClass: KClass<T>, encoded: String) =
            Single.defer { Single.just(gson.fromJson(encoded, modelClass.java)) }
}
