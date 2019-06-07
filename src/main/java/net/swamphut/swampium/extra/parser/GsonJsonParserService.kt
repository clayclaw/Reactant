package net.swamphut.swampium.extra.parser

import com.google.gson.GsonBuilder
import io.reactivex.Single
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.service.spec.parser.JsonParserService
import kotlin.reflect.KClass

@SwObject
class GsonJsonParserService : JsonParserService {
    private val gson = GsonBuilder().setPrettyPrinting().create()!!

    override fun encode(obj: Any): Single<String> =
            Single.defer { Single.just(gson.toJson(obj)) }

    override fun <T : Any> decode(modelClass: KClass<T>, encoded: String) =
            Single.defer { Single.just(gson.fromJson(encoded, modelClass.java)) }
}
