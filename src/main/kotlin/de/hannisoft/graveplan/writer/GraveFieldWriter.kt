package de.hannisoft.de.hannisoft.graveplan.writer

import de.hannisoft.graveplan.model.ElementType
import de.hannisoft.graveplan.model.GraveField
import de.hannisoft.graveplan.model.PlanElement
import de.hannisoft.graveplan.properties.ElementsReader

class GraveFieldWriter(val outputDirString: String) {
    fun write(graveFields: Map<String, GraveField>, dueDay: String, allData: Boolean) {
        // PNGGraveMapWriter png = new PNGGraveMapWriter();
        val fieldElements = ElementsReader().readElements(ElementType.FELD)
        if (allData) {
            writeHtmlGraveFields(OutputType.RUNTIME, graveFields, fieldElements, dueDay, true )
            writeHtmlGraveSiteFiles(graveFields, dueDay)
        }
        writeHtmlGraveFields(OutputType.REFERENCE, graveFields, fieldElements, dueDay, allData )
        writeHtmlSearchSiteFiles(graveFields, dueDay, allData)
    }

    private fun writeHtmlGraveFields(outputType: OutputType, graveFields: Map<String, GraveField>, fieldElements: Map<String, PlanElement>, dueDay: String, allData: Boolean) {
        val writer = HTMLGraveFieldWriter(outputDirString, outputType)
        graveFields.values.forEach { graveField ->
            graveField.finishEdit(fieldElements)
            writer.write(graveField, dueDay, allData)
        }
    }

    private fun writeHtmlGraveSiteFiles(graveFields: Map<String, GraveField>, dueDay: String) {
        val writer = HTMLGraveSiteWriter(outputDirString)
        graveFields.values.forEach { graveField -> writer.write(graveField.graveSites(), dueDay) }
    }

    private fun writeHtmlSearchSiteFiles(graveFields: Map<String, GraveField>, dueDay: String, allData: Boolean) {
        HTMLSearchSiteWrite(outputDirString).write(graveFields, dueDay, allData)
    }

}