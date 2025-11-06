package de.hannisoft.de.hannisoft.graveplan.writer

import de.hannisoft.graveplan.model.GraveField
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class GravePlanWriter(outputDir: String, timeStampString: String) {

    val outputDir: String = outputDir.trimEnd('/') + '/'
    val outputDirPrivate: String = "${outputDir}Friedhofsplan_$timeStampString/"
    val outputDirPublic: String = "${outputDir}Friedhofsplan_öffentlich_$timeStampString/"
    val dueDate: String = LocalDate.parse(timeStampString, DateTimeFormatter.ofPattern("yyyyMMdd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

    fun export(graveFields: Map<String, GraveField>) {
        export(outputDirPrivate, graveFields, true)
        export(outputDirPublic, graveFields, false)
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
        GraveFieldWriter(outputDir).write(graveFields, dueDay = dueDate, allData = allData)
        zipDirectory(outputDir)
    }

    fun zipDirectory(sourceDir: String) {
        val sourceDirPath = Path.of(sourceDir)
        val zipFile = sourceDirPath.parent.resolve("${sourceDirPath.fileName}.zip")
        ZipOutputStream(Files.newOutputStream(zipFile)).use { zipOut ->
            Files.walk(sourceDirPath).filter { path -> !Files.isDirectory(path) }.forEach { file ->
                val zipEntry = ZipEntry(sourceDirPath.relativize(file).toString().replace("\\", "/"))
                zipOut.putNextEntry(zipEntry)
                Files.newInputStream(file).use { input -> input.copyTo(zipOut) }
                zipOut.closeEntry()
            }
        }
        println("Created zip file ${zipFile.fileName}")
    }
}