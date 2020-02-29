package dev.reactant.reactant.utils.content.area

import dev.reactant.reactant.extensions.rangeTo
import dev.reactant.reactant.extensions.snapToBlockCenter
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

interface Area : Cloneable {
    operator fun contains(loc: Location) = contains(loc.toVector())
    operator fun contains(vec: Vector): Boolean
    operator fun contains(entity: Entity) = contains(entity.location)
    operator fun contains(block: Block) = contains(block.location.apply { snapToBlockCenter() })

    operator fun plus(vector: Vector): Area = clone().also { move(vector) }

    fun move(vector: Vector)

    fun toWorldArea(world: World) = WorldArea(world, this)

    val bounds: Pair<Vector, Vector>

    /**
     * Get the list of area included integer vectors (as know as block location)
     */
    val integerVectors: List<Vector> get() = bounds.run { first..second }.integerVectors.filter { contains(it) }


    public override fun clone(): Area
}
