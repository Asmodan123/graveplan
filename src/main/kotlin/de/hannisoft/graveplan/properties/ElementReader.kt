package de.hannisoft.graveplan.properties

import de.hannisoft.graveplan.model.ElementType
import de.hannisoft.graveplan.model.PlanElement
import java.util.*

class ElementsReader {
    fun readElements(): MutableMap<Int, PlanElement> {
        val elements: MutableMap<Int, PlanElement> = HashMap<Int, PlanElement>()
        val props = Properties()
        props.load(javaClass.getClassLoader().getResourceAsStream("raw/elements.properties"))
        for (prop in props.entries) {
            try {
                val elementId = prop.key.toString().toInt()
                val values = prop.value.toString().split(",")
                val type = values[0]
                val pEelement = PlanElement(elementId, type)
                pEelement.minRow = values[1].toInt()
                pEelement.maxRow = values[2].toInt()
                pEelement.minPlace = values[3].toInt()
                pEelement.maxPlace = values[4].toInt()
                if (values.size > 5) {
                    pEelement.name = values[5]
                }
                if (values.size > 6) {
                    pEelement.title = values[6]
                }
                elements[elementId] = pEelement
            } catch (e: Exception) {
                println("Can't read ElementProperty: '$prop': ${e.message}")
                e.printStackTrace()
            }
        }
        return elements
    }

    fun readElements(type: ElementType): MutableMap<String?, PlanElement?> {
        val typeElements: MutableMap<String?, PlanElement?> = HashMap<String?, PlanElement?>()
        for (element in readElements().values) {
            if (type == element.type) {
                typeElements[element.name] = element
            }
        }
        return typeElements
    }
}