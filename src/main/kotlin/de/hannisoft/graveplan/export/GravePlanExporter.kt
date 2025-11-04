package de.hannisoft.de.hannisoft.graveplan.export

import de.hannisoft.graveplan.model.GraveSite
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.jvm.java

class GravePlanExporter(outputDir: String, var timestring: String) {

    val outputDir: String = outputDir.trimEnd('/') + '/'
    var outputDirPrivate: String = "${outputDir}Friedhofsplan_$timestring/"
    var outputDirPublic: String = "${outputDir}Friedhofsplan_öffentlich_$timestring/"

    fun export(graveSites: Map<String, GraveSite>) {
        export(outputDirPrivate, graveSites, true)
        export(outputDirPublic, graveSites, false)
    }

    private fun export(outputDir: String, graveSites: Map<String, GraveSite>, isFullExport: Boolean) {
        copyResourceFiles(outputDir)
        HtmlRootPageWriter().write(outputDir, "Friedhofsplan.html", timestring)
        zipDirectory(outputDir)
    }

    private fun copyResourceFiles(oputputDir: String) {
        val resourcesDir = File(GravePlanExporter::class.java.getResource("")?.path ?: "src/main/resources")
        val resourceFiles = listOf(
            "plan/js/bootstrap.min.css",
            "plan/js/bootstrap.bundle.min.js",
            "plan/js/vis-network.min.js",
            "plan/plan.ico",
            "plan/suche/search.css",
            "plan/suche/search.js",
            "plan/suche/searchicon.png",
            "plan/nav.css",
            "plan/satellit.png",
            "plan/Friedhofsplan.svg"
        )

        resourceFiles.forEach { fileName ->
            val resourceFile = File(resourcesDir, fileName)
            val targetFile = File(oputputDir, fileName)

            if (resourceFile.exists()) {
                targetFile.parentFile.mkdirs()
                Files.copy(resourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } else {
                // Fallback: Try to copy from classpath resources
                this::class.java.classLoader.getResourceAsStream(fileName)?.use { inputStream ->
                    targetFile.parentFile.mkdirs()
                    Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }
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