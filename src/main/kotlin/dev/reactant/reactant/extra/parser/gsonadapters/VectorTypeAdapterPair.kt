package dev.reactant.reactant.extra.parser.gsonadapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.layers.SystemLevel
import org.bukkit.util.Vector

@Component
class VectorTypeAdapterPair : TypeAdapterPair, SystemLevel {

    class VectorTypeAdapter: TypeAdapter<Vector>() {

        override fun write(writer: JsonWriter, vector: Vector?) {
            when (vector) {
                null -> writer.nullValue()
                else -> writer.apply {
                    beginObject()

                    name("x")
                    value(vector.x)

                    name("y")
                    value(vector.y)

                    name("z")
                    value(vector.z)

                    endObject()
                }
            }
        }

        override fun read(reader: JsonReader): Vector? = when (reader.peek()) {
            JsonToken.NULL -> null
            else -> reader.run {
                beginObject()

                var x: Double = 0.0
                var y: Double = 0.0
                var z: Double = 0.0

                while (hasNext()) {
                    val name = nextName()
                    when (name) {
                        "x" -> x = nextDouble()
                        "y" -> y = nextDouble()
                        "z" -> z = nextDouble()
                    }
                }
                endObject()
                Vector(x, y, z)
            }

        }
    }

    override val type = Vector::class.java
    override val typeAdapter = VectorTypeAdapter()

}
