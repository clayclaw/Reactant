package dev.reactant.reactant.extra.parser

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.layers.SystemLevel
import dev.reactant.reactant.service.spec.parser.TomlParserService
import io.reactivex.rxjava3.core.Single
import kotlin.reflect.KClass

@Component
open class Toml4jTomlParserService : TomlParserService, SystemLevel {
    protected val toml = Toml()
    protected val tomlWriter = TomlWriter()

    override fun encode(obj: Any): Single<String> = Single.defer { Single.just(tomlWriter.write(obj)) }

    override fun <T : Any> decode(encoded: String, modelClass: KClass<T>): Single<T> =
            Single.defer { Single.just(toml.read(encoded).to(modelClass.java)) }

}
