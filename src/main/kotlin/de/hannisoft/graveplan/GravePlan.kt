package de.hannisoft.graveplan

import de.hannisoft.graveplan.excelimport.GraveFilesImporter
import de.hannisoft.graveplan.properties.WebsiteConfigReader
import de.hannisoft.graveplan.writer.GravePlanWriter

fun main() {
    val importDir = "/Users/jahlers/tmp/plan/export/"
    val outputDir = "/Users/jahlers/tmp/plan/"
    val websiteConfig = WebsiteConfigReader().read()
    GraveFilesImporter(importDir)
        .import().apply {
            GravePlanWriter(outputDir, this.dueDate, websiteConfig)
                .export(this.gravesMap)
    }
}
