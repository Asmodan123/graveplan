package de.hannisoft.graveplan.properties

import de.hannisoft.graveplan.model.PlanElement
import de.hannisoft.graveplan.model.Point
import java.io.IOException
import java.util.*

class ElementPointsReader {

    @Throws(IOException::class)
    fun readElementPoints(elements: Map<Int, PlanElement>, points: Map<Int, Point> ) {
        val props = Properties()
        props.load(javaClass.getClassLoader().getResourceAsStream("raw/elementPoints.properties"))
        for (prop in props.entries) {
            try {
                val elementId = prop.key.toString().toInt()
                val pElement = elements[elementId]
                if (pElement != null) {
                    prop.value.toString()
                        .split(",")
                        .map { it.toInt() }
                        .forEach {
                            val point = points[it]
                            if (point != null) {
                                pElement.addPoint(point)
                            } else {
                                println("Pint with id $it not found")
                            }
                        }
                } else {
                    println("Element with id $elementId not found")
                }
            } catch (e: Exception) {
                println("Can't read ElementPoints of '$prop': ${e.message}")
                e.printStackTrace()
            }
        }
    }
}