package de.hannisoft.de.hannisoft.graveplan.writer

import de.hannisoft.graveplan.model.ElementType
import de.hannisoft.graveplan.model.GraveMap
import de.hannisoft.graveplan.model.PlanElement
import de.hannisoft.graveplan.properties.ElementsReader

class GraveMapWriter(val outputDirString: String) {
    fun write(graveMaps: Map<String, GraveMap>, dueDay: String, allData: Boolean) {
        // PNGGraveMapWriter png = new PNGGraveMapWriter();
        val fieldElements = ElementsReader().readElements(ElementType.FELD)
        if (allData) {
            writeHtmlGraveMap(OutputType.RUNTIME, graveMaps, fieldElements, dueDay, true )
            writeHtmlGraveSiteFiles(graveMaps, dueDay)
        }
        writeHtmlGraveMap(OutputType.REFERENCE, graveMaps, fieldElements, dueDay, allData )
        writeHtmlSearchSiteFiles(graveMaps, dueDay, allData)
    }

    private fun writeHtmlGraveMap(outputType: OutputType, graveMaps: Map<String, GraveMap>, fieldElements: Map<String, PlanElement>, dueDay: String, allData: Boolean) {
        val writer = HTMLGraveMapWriter(outputDirString, outputType)
        graveMaps.values.forEach { graveMap ->
            graveMap.finishEdit(fieldElements)
            writer.write(graveMap, dueDay, allData)
        }
    }

    private fun writeHtmlGraveSiteFiles(graveMaps: Map<String, GraveMap>, dueDay: String) {
        val writer = HTMLGraveSiteWriter(outputDirString)
        graveMaps.values.forEach { graveMap -> writer.write(graveMap.graveSites(), dueDay) }
    }

    private fun writeHtmlSearchSiteFiles(graveMaps: Map<String, GraveMap>, dueDay: String, allData: Boolean) {
        HTMLSearchSiteWrite(outputDirString).write(graveMaps, dueDay, allData)
    }

}