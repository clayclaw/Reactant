package net.swamphut.swampium.extra.parser

import io.reactivex.Single
import net.swamphut.swampium.core.dependency.provide.ServiceProvider
import net.swamphut.swampium.core.swobject.container.SwObject
import net.swamphut.swampium.service.spec.parser.YamlParserService
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer

@SwObject
@ServiceProvider([YamlParserService::class])
class SnakeYamlParserService : YamlParserService {
    private val representer = Representer()
    private val yaml = Yaml(CustomClassLoaderConstructor(this.javaClass.classLoader), representer)
    override fun encode(obj: Any): Single<String> =
            Single.defer {
                representer.addClassTag(obj::class.java, Tag.MAP)
                Single.just(yaml.dump(obj))
            }

    override fun <T> decode(modelClass: Class<T>, encoded: String) =
            Single.defer { Single.just(yaml.loadAs(encoded, modelClass)) }

}
