package io.reactant.reactant.extra.parser

import io.reactant.reactant.core.component.Component
import io.reactant.reactant.service.spec.parser.YamlParserService
import io.reactivex.Single
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer
import kotlin.reflect.KClass

@Component
class SnakeYamlParserService : YamlParserService {
    private val representer = Representer()
    private val yaml = Yaml(CustomClassLoaderConstructor(this.javaClass.classLoader), representer)
    override fun encode(obj: Any): Single<String> =
            Single.defer {
                representer.addClassTag(obj::class.java, Tag.MAP)
                Single.just(yaml.dump(obj))
            }

    override fun <T : Any> decode(modelClass: KClass<T>, encoded: String) =
            Single.defer { Single.just(yaml.loadAs(encoded, modelClass.java)) }

}
