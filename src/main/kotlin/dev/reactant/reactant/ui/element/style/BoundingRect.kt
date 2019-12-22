package dev.reactant.reactant.ui.element.style

data class BoundingRect(
        val top: Int,
        val right: Int,
        val bottom: Int,
        val left: Int
) {
    fun toPositions(): Set<Pair<Int, Int>> = (if (top < bottom) (top until bottom) else IntRange.EMPTY).flatMap { row ->
        (if (left < right) (left until right) else IntRange.EMPTY).map { col -> col to row /* x, y */ }
    }.toSet()

    fun contains(position: Pair<Int, Int>): Boolean =
            position.first in left until right && position.second in top until bottom
}
