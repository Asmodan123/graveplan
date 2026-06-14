package de.hannisoft.graveplan.writer

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class FileExporter {
    fun exportFiles(outputDirString: String, sourceFiles: List<String>) {
        val outputDir = File(outputDirString)
        sourceFiles.forEach { exportFile(outputDir, it)}
    }

    fun exportFile(outputDir: File, sourceFile: String, additionalContent: String = "") {
        val resourcesDir = File("src/main/resources")
        val resourceFile = File(resourcesDir, sourceFile)
        val targetFile = File(outputDir, sourceFile)

        if (resourceFile.exists()) {
            targetFile.parentFile.mkdirs()
            Files.copy(resourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } else {
            // Fallback: Try to copy from classpath resources
            this::class.java.classLoader.getResourceAsStream(sourceFile)?.use { inputStream ->
                targetFile.parentFile.mkdirs()
                Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
        if (additionalContent.isNotEmpty()) {
            targetFile.appendText(additionalContent)
        }
    }
}

fun initOutputDir(outputDir: File) {
    outputDir.mkdirs()
    outputDir.listFiles()?.forEach { file ->
        if (!file.delete()) {
            System.err.println("Could not delete old file: ${file.absolutePath}")
        }
    }
}