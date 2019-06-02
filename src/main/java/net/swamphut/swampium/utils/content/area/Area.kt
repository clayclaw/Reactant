package net.swamphut.swampium.utils.content.area

import net.swamphut.swampium.extensions.snapToBlockCenter
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

    operator fun plus(vector: Vector) = clone().move(vector)

    fun move(vector: Vector)

    fun toWorldArea(world: World) = WorldArea(world, this)
    public override fun clone(): Area
}
