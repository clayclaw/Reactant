package dev.reactant.reactant.extra.parser.gsonadapters

import com.google.gson.InstanceCreator
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.layers.SystemLevel
import java.lang.reflect.ParameterizedType
import java.util.*

@Component
class EnumMapGsonTypeAdapterPair : TypeAdapterPair, SystemLevel {
    /**
     * Dummy enum class
     */
    enum class X {}

    override val type = EnumMap::class.java
    override val typeAdapter: InstanceCreator<EnumMap<*, *>> = InstanceCreator {
        val clazz = ((it as ParameterizedType).actualTypeArguments[0]!! as Class<Any>)
        return@InstanceCreator EnumMap<X, Any>(clazz as Class<X>)
    }
}
