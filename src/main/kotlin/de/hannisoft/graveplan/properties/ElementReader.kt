package de.hannisoft.graveplan.properties

import de.hannisoft.graveplan.model.ElementType
import de.hannisoft.graveplan.model.PlanElement
import java.util.*

class ElementsReader {
    fun readElements(): Map<Int, PlanElement> {
        val props = Properties()
        props.load(javaClass.getClassLoader().getResourceAsStream("raw/elements.properties"))
        return props.entries
            .associate { (key, value) ->
                key.toString().toInt() to
                createPlanElement(key.toString(), value.toString()) }
    }

    fun createPlanElement(elementId: String, value: String): PlanElement {
        val values = value.toString().split(",")
        val type = values[0]
        val pEelement = PlanElement(elementId.toInt(), type)
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
        return pEelement
    }

    fun readElements(type: ElementType) =
        readElements().values
            .filter { element -> element.type == type }
            .associateBy { element -> element.name!! }

}