package de.hannisoft.graveplan.writer

import de.hannisoft.graveplan.model.GraveField
import de.hannisoft.graveplan.properties.WebsiteConfig
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GravePlanWriter(outputDir: String, val timeStampString: String, val websiteConfig: WebsiteConfig) {

    val websiteDir: String = outputDir.trimEnd('/') + "/website/"
    val outputDirPrivate: String = "$websiteDir$PLAN_DIR/$INTERNAL_DIR/$timeStampString/"
    val outputDirPublic: String = "$websiteDir$PLAN_DIR/$PUBLIC_DIR/$timeStampString/"
    val dueDate: String = LocalDate.parse(timeStampString, DateTimeFormatter.ofPattern("yyyyMMdd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

    fun export(graveFields: Map<String, GraveField>) {
        export(outputDirPrivate, graveFields, true)
        export(outputDirPublic, graveFields, false)
        WebsiteWriter(websiteDir, websiteConfig).write(timeStampString, dueDate)
    }

    private fun export(outputDir: String, graveFields: Map<String, GraveField>, allData: Boolean) {
        FileExporter().exportFiles(outputDir, listOf(
            "plan/js/bootstrap.min.css",
            "plan/js/bootstrap.bundle.min.js",
            "plan/js/vis-network.min.js",
            "plan/plan.ico",
            "suche/search.css",
            "suche/search.js",
            "suche/searchicon.png",
            "plan/nav.css",
            "plan/satellit.png",
            "plan/Friedhofsplan.svg"
        ))
        HtmlRootPageWriter().write(outputDir, "Friedhofsplan.html", dueDate)
        GraveFieldWriter(outputDir).write(graveFields, dueDate, allData)
        println("Created plan in $outputDir")
    }
}
