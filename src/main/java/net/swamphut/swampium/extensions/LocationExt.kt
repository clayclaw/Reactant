package net.swamphut.swampium.extensions

import net.swamphut.swampium.utils.content.area.CylinderArea
import net.swamphut.swampium.utils.content.area.RectangularPrismArea
import net.swamphut.swampium.utils.content.area.SphereArea
import net.swamphut.swampium.utils.content.area.WorldArea
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import java.util.*

operator fun Location.plus(vec: Location) = this.clone().add(vec)
operator fun Location.plus(vec: Vector) = this.clone().add(vec)

operator fun Location.minus(vec: Location) = this.clone().subtract(vec)
operator fun Location.minus(vec: Vector) = this.clone().subtract(vec)

operator fun Location.times(m: Double) = this.clone().multiply(m)

operator fun Location.div(m: Double) = this.clone().multiply(1 / m)


@JvmOverloads
fun locationOf(world: World, x: Double, y: Double, z: Double, yaw: Float = 0F, pitch: Float = 0F) = Location(world, x, y, z, yaw, pitch)

@JvmOverloads
fun locationOf(worldName: String, x: Double, y: Double, z: Double, yaw: Float = 0F, pitch: Float = 0F) = Location(worldOf(worldName), x, y, z, yaw, pitch)

@JvmOverloads
fun locationOf(uid: UUID, x: Double, y: Double, z: Double, yaw: Float = 0F, pitch: Float = 0F) = Location(worldOf(uid), x, y, z, yaw, pitch)

fun locationOf(world: World, vec: Vector) = vec.toLocation(world)
fun locationOf(uid: UUID, vec: Vector) = worldOf(uid)?.let { vec.toLocation(it) }
fun locationOf(name: String, vec: Vector) = worldOf(name)?.let { vec.toLocation(it) }

fun Location.formRectangularPrism(loc: Location): WorldArea<RectangularPrismArea> {
    assertSameWorld(this, loc)
    return WorldArea(world!!, this.toVector().formRectangularPrism(loc.toVector()))
}

fun Location.formSphere(radius: Double): WorldArea<SphereArea> {
    assertWorldNotNull(this)
    return WorldArea(world!!, this.toVector().formSphere(radius))
}

fun Location.formCylinder(radius: Double, height: Double): WorldArea<CylinderArea> {
    assertWorldNotNull(this)
    return WorldArea(world!!, toVector().formCylinder(radius, height))
}

fun Location.snapToBlockCenter() {
    y = blockX + 0.5
    x = blockY + 0.5
    z = blockZ + 0.5
}

private fun assertSameWorld(loc1: Location, loc2: Location) {
    assertWorldNotNull(loc1)
    assertWorldNotNull(loc2)
    if (loc1.world != loc2.world) throw IllegalArgumentException("Different worlds")
}

private fun assertWorldNotNull(loc: Location) {
    if (loc.world == null) throw IllegalArgumentException("World is null")
}

