package de.hannisoft.de.hannisoft.graveplan.writer

import de.hannisoft.graveplan.model.GraveMap
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class GravePlanWriter(outputDir: String, var dueDate: String) {

    val outputDir: String = outputDir.trimEnd('/') + '/'
    var outputDirPrivate: String = "${outputDir}Friedhofsplan_$dueDate/"
    var outputDirPublic: String = "${outputDir}Friedhofsplan_öffentlich_$dueDate/"

    fun export(gravesMap: Map<String, GraveMap>) {
        export(outputDirPrivate, gravesMap, true)
        export(outputDirPublic, gravesMap, false)
    }

    private fun export(outputDir: String, gravesMap: Map<String, GraveMap>, allData: Boolean) {
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
        GraveMapWriter(outputDir).write(gravesMap, dueDay = dueDate, allData = allData)
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