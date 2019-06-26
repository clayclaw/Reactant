package io.reactant.reactant.utils.content.area

import io.reactant.reactant.extensions.plus
import org.bukkit.util.Vector

class CombinedArea(val areaA: Area, val areaB: Area, val operator: Operator) : Area {
    enum class Operator {
        UNION, INTERSECTION, EXCLUSION, DIFFERENCE
    }

    var movedOffset = Vector()
        private set

    override fun contains(vec: Vector): Boolean {
        val inA = vec in areaA
        val inB = vec in areaB
        return when (operator) {
            Operator.UNION -> inA || inB
            Operator.INTERSECTION -> inA && inB
            Operator.EXCLUSION -> inA && !inB || inB && !inA
            Operator.DIFFERENCE -> inA && !inB
        }
    }

    override fun move(vector: Vector) {
        movedOffset += vector
    }

    override fun clone(): Area = CombinedArea(areaA.clone(), areaB.clone(), operator)
}

fun Area.union(target: Area) = CombinedArea(this, target, CombinedArea.Operator.UNION)
fun Area.intersection(target: Area) = CombinedArea(this, target, CombinedArea.Operator.INTERSECTION)
fun Area.exclusion(target: Area) = CombinedArea(this, target, CombinedArea.Operator.EXCLUSION)
fun Area.difference(target: Area) = CombinedArea(this, target, CombinedArea.Operator.DIFFERENCE)

fun WorldArea<Area>.union(target: Area): WorldArea<CombinedArea> = WorldArea(world, CombinedArea(this, target, CombinedArea.Operator.UNION))
fun WorldArea<Area>.intersection(target: Area): WorldArea<CombinedArea> = WorldArea(world, CombinedArea(this, target, CombinedArea.Operator.INTERSECTION))
fun WorldArea<Area>.exclusion(target: Area): WorldArea<CombinedArea> = WorldArea(world, CombinedArea(this, target, CombinedArea.Operator.EXCLUSION))
fun WorldArea<Area>.difference(target: Area): WorldArea<CombinedArea> = WorldArea(world, CombinedArea(this, target, CombinedArea.Operator.DIFFERENCE))
