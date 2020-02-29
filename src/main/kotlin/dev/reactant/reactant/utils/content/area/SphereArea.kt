package dev.reactant.reactant.utils.content.area

import dev.reactant.reactant.extensions.plus
import org.bukkit.util.Vector

open class SphereArea(var center: Vector, var radius: Double) : Area {
    override fun move(vector: Vector) {
        this.center += vector
    }

    override val bounds: Pair<Vector, Vector>
        get() = center + Vector(-radius, -radius, -radius) to center + Vector(radius, radius, radius)

    override fun contains(vec: Vector): Boolean = vec.isInSphere(center, radius)

    override fun clone(): SphereArea = SphereArea(center, radius)
}
