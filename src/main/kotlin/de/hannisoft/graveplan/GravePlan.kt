package de.hannisoft.de.hannisoft.graveplan

import de.hannisoft.de.hannisoft.graveplan.excelimport.GraveFilesImporter
import de.hannisoft.de.hannisoft.graveplan.export.GravePlanExporter

fun main() {
    val importDir = "/Users/jah/tmp/plan/MyHadesExport"
    val outputDir = "/Users/jah/tmp/plan/"
    GraveFilesImporter(importDir)
        .import().apply {
            GravePlanExporter(outputDir, this.timestring)
                .export(this.graveSites)
    }
}