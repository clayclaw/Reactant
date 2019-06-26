package net.swamphut.swampium.utils.content.area

import org.bukkit.Location
import org.bukkit.World

open class WorldArea<out T : Area>(var world: World, val area: T) : Area by area {
    override fun clone(): WorldArea<T> = WorldArea(world, area);
    override fun contains(loc: Location): Boolean = loc.world == world && super.contains(loc)

    val entities get() = world.entities.filter { it in this }
    val livingEntities get() = world.livingEntities.filter { it in this }
    val players get() = world.players.filter { it in this }

    @Suppress("UNCHECKED_CAST")
    fun toArea(): T = area.clone() as T
}
