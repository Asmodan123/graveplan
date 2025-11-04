package de.hannisoft.de.hannisoft.graveplan.excelimport

import de.hannisoft.graveplan.excelimport.GraveFileReader
import de.hannisoft.graveplan.excelimport.GraveSiteCriteriaFileReader
import de.hannisoft.graveplan.model.GraveSite
import java.io.File

class GraveFilesImporter(importDir : String) {
    companion object {
        const val FILE_TOKEN_GRAVE_SITE: String = "Grabstätten_"
        const val FILE_TOKEN_GRAVE: String = "Verstorbene_"
        const val FILE_TOKEN_CRITERIA: String = "Auswahlkriterien_"
        const val FILE_ENDING: String = ".xls"
    }
    val importDir: String = importDir.trimEnd('/') + '/'
    val graveSites = mutableMapOf<String, GraveSite>()
    lateinit var timestring: String

    fun import(timestring: String = findNewestTimeString(importDir)): GraveFilesImporter  {
        this.timestring = timestring
        println("Start import from $importDir with timestamp-suffix $timestring")
        val graveSites = GraveSiteFileReader().read(File(importDir + FILE_TOKEN_GRAVE_SITE + timestring + FILE_ENDING))
        GraveFileReader().read(File(importDir + FILE_TOKEN_GRAVE + timestring + FILE_ENDING), graveSites)
        GraveSiteCriteriaFileReader().read(File(importDir + FILE_TOKEN_CRITERIA + timestring + FILE_ENDING), graveSites)
        return this
    }
}

fun findNewestTimeString(importDir: String): String {
    val regex = Regex("""(\d{8})(?=\.[^.]+$)""") // 8 Ziffern vor der Dateiendung
    return File(importDir).listFiles()
        ?.mapNotNull { file ->
            regex.find(file.name)?.value
        }
        ?.maxOrNull()
        ?: ""

}
