package net.swamphut.swampium.extra.parser

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import io.reactivex.Single
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.service.spec.parser.TomlParserService
import kotlin.reflect.KClass

@SwObject
class Toml4jTomlParserService : TomlParserService {
    override fun encode(obj: Any): Single<String> = Single.defer { Single.just(TomlWriter().write(obj)) }

    override fun <T : Any> decode(modelClass: KClass<T>, encoded: String): Single<T> =
            Single.defer { Single.just(Toml().read(encoded).to(modelClass.java)) }

}
