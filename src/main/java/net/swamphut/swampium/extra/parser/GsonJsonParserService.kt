package net.swamphut.swampium.extra.parser

import com.google.gson.GsonBuilder
import io.reactivex.Single
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.core.swobject.dependency.ServiceProvider
import net.swamphut.swampium.core.swobject.lifecycle.LifeCycleHook
import net.swamphut.swampium.service.spec.parser.JsonParserService

@SwObject
@ServiceProvider(provide = [JsonParserService::class])
class GsonJsonParserService : JsonParserService, LifeCycleHook {
    protected val gson = GsonBuilder().setPrettyPrinting().create()

    override fun encode(obj: Any): Single<String> = Single.defer { Single.just(gson.toJson(obj)) }

    override fun <T> decode(modelClass: Class<T>, encoded: String): Single<T> =
            Single.defer { Single.just(gson.fromJson(encoded, modelClass)) }
}
