package dev.reactant.reactant.extensions

import dev.reactant.reactant.utils.content.area.CylinderArea
import dev.reactant.reactant.utils.content.area.RectangularPrismArea
import dev.reactant.reactant.utils.content.area.SphereArea
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.ceil
import kotlin.math.floor

operator fun Vector.plus(vec: Vector) = this.clone().add(vec)
operator fun Vector.plus(vec: Location) = this.clone().add(vec.toVector())

operator fun Vector.minus(vec: Vector) = this.clone().subtract(vec)
operator fun Vector.minus(vec: Location) = this.clone().subtract(vec.toVector())

operator fun Vector.times(m: Double) = this.clone().multiply(m)

operator fun Vector.div(m: Double) = this.clone().multiply(1 / m)

operator fun Vector.rangeTo(vec: Vector) = formRectangularPrism(vec)

val Vector.ceil get() = Vector(ceil(this.x), ceil(this.y), ceil(this.z))
val Vector.floor get() = Vector(floor(this.x), floor(this.y), floor(this.z))

fun Vector.formRectangularPrism(vec: Vector) = RectangularPrismArea(this, vec)
fun Vector.formSphere(radius: Double) = SphereArea(this, radius)
fun Vector.formCylinder(radius: Double, height: Double) = CylinderArea(this, radius, height)
