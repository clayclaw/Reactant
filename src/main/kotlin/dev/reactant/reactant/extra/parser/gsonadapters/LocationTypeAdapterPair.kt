package dev.reactant.reactant.extra.parser.gsonadapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.layers.SystemLevel
import dev.reactant.reactant.extensions.locationOf
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.util.*

@Component
class LocationTypeAdapterPair : TypeAdapterPair, SystemLevel {
    class LocationTypeAdapter : TypeAdapter<Location>() {
        override fun write(writer: JsonWriter, location: Location?) {
            when (location) {
                null -> writer.nullValue()
                else -> writer.apply {
                    beginObject()

                    name("world")
                    location.world?.uid?.toString().let { value(it) } ?: nullValue()

                    name("x")
                    value(location.x)

                    name("y")
                    value(location.y)

                    name("z")
                    value(location.z)

                    name("yaw")
                    value(location.yaw)

                    name("pitch")
                    value(location.pitch)

                    endObject()
                }
            }
        }

        override fun read(reader: JsonReader): Location? = when (reader.peek()) {
            JsonToken.NULL -> null
            else -> reader.run {
                beginObject()

                var world: World? = null
                var x: Double = 0.0
                var y: Double = 0.0
                var z: Double = 0.0
                var yaw: Float = 0.0F
                var pitch: Float = 0.0F

                while (hasNext()) {
                    val name = nextString()
                    when (name) {
                        "world" -> world = Bukkit.getWorld(UUID.fromString(nextString()))
                        "x" -> x = nextDouble()
                        "y" -> y = nextDouble()
                        "z" -> z = nextDouble()
                        "yaw" -> yaw = nextDouble().toFloat()
                        "pitch" -> pitch = nextDouble().toFloat()
                    }
                }
                endObject()
                locationOf(world, x, y, z, yaw, pitch)
            }

        }
    }

    override val type = Location::class.java
    override val typeAdapter = LocationTypeAdapter()
}
