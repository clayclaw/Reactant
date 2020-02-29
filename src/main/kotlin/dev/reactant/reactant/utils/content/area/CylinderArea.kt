package dev.reactant.reactant.utils.content.area

import dev.reactant.reactant.extensions.plus
import org.bukkit.util.Vector
import kotlin.math.pow

class CylinderArea(var center: Vector, var radius: Double, var height: Double) : Area {
    override fun move(vector: Vector) {
        center += vector
    }

    override val bounds: Pair<Vector, Vector>
        get() = center + Vector(-radius, 0.0, -radius) to center + Vector(radius, height, radius)

    override fun contains(vec: Vector): Boolean = (vec.x - center.x).pow(2) + (vec.y - center.y).pow(2) <= radius.pow(2) && vec.y in center.y..(center.y + height)

    override fun clone(): SphereArea = SphereArea(center, radius)
}
