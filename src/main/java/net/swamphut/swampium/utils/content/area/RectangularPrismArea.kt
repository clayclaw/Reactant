package net.swamphut.swampium.utils.content.area

import org.bukkit.World
import org.bukkit.util.Vector

open class RectangularPrismArea(var x1: Double, var y1: Double, var z1: Double,
                                var x2: Double, var y2: Double, var z2: Double) : Area {
    override fun move(vector: Vector) {
        x1 += vector.x
        x2 += vector.x
        y1 += vector.y
        y2 += vector.y
        z1 += vector.z
        z2 += vector.z
    }

    override fun clone(): RectangularPrismArea = RectangularPrismArea(x1, y1, z1, x2, y2, z2)

    constructor(vec1: Vector, vec2: Vector) : this(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z)

    val minCorner get() = Vector(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2))
    val maxCorner get() = Vector(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))

    override operator fun contains(vec: Vector): Boolean = vec.isInAABB(minCorner, maxCorner)

    override fun toWorldArea(world: World) = WorldArea(world, this)
}
