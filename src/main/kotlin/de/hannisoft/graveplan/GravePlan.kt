package de.hannisoft.de.hannisoft.graveplan

import de.hannisoft.de.hannisoft.graveplan.excelimport.GraveFilesImporter
import de.hannisoft.de.hannisoft.graveplan.writer.GravePlanWriter

fun main() {
    val importDir = "/Users/jah/tmp/plan/MyHadesExport"
    val outputDir = "/Users/jah/tmp/plan/"
    GraveFilesImporter(importDir)
        .import().apply {
            GravePlanWriter(outputDir, this.timestring)
                .export(this.gravesMap)
    }
}