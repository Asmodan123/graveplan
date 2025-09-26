package de.hannisoft.graveplan.properties

import de.hannisoft.graveplan.model.Point
import java.util.*

class PointsReader(private val xFactor: Double, private val yFactor: Double, private val xDelta: Int, private val yDelta: Int, private val rotation: Double) {
    fun readPoints(): MutableMap<Int?, Point?> {
        val points: MutableMap<Int?, Point?> = HashMap<Int?, Point?>()
        var minX = 0
        var maxX = 0
        var minY = 0
        var maxY = 0
        val props = Properties()
        props.load(javaClass.getClassLoader().getResourceAsStream("plan/points.properties"))
        for (prop in props.entries) {
            try {
                val pointId = prop.key.toString().toInt()
                val coords = prop.value.toString().split(",")
                val x = coords[0].toInt()
                val y = coords[1].toInt()
                val point = Point(pointId, x, y)
                point.scale(xFactor, yFactor)
                point.move(xDelta, yDelta)
                point.rotate(rotation)
                points[pointId] = point
                if (minX == 0 || point.x < minX) {
                    minX = point.x
                }
                if (maxX == 0 || point.x > maxX) {
                    maxX = point.x
                }
                if (minY == 0 || point.y < minY) {
                    minY = point.y
                }
                if (maxY == 0 || point.y > maxY) {
                    maxY = point.y
                }
            } catch (e: Exception) {
                System.err.println("Can't convert Property to Point of '$prop': ${e.message}")
                e.printStackTrace()
            }
        }
        println("x:[$minX $maxX]")
        println("y:[$minY $maxY]")
        return points
    }
}