package de.hannisoft.graveplan.model

import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Point(
    val id: Int,
    var x: Int,
    var y: Int
) {

    fun rotate(degree: Double) {
        if (degree == 0.0) return

        val xOld = x.toDouble()
        val yOld = y.toDouble()

        val xd = xOld * cos(degree) - yOld * sin(degree)
        val yd = xOld * sin(degree) + yOld * cos(degree)

        x = xd.roundToInt()
        y = yd.roundToInt()
    }

    fun scale(xFactor: Double, yFactor: Double) {
        x = (x * xFactor).roundToInt()
        y = (y * yFactor).roundToInt()
    }

    fun move(xDelta: Int, yDelta: Int) {
        x += xDelta
        y += yDelta
    }

    override fun toString(): String = "$id=$x,$y"
}