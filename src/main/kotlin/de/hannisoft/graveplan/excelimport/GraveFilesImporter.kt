package de.hannisoft.graveplan.excelimport

import de.hannisoft.graveplan.excelimport.GraveFilesImporter.Companion.format
import de.hannisoft.graveplan.excelimport.GraveFilesImporter.Companion.format2
import de.hannisoft.graveplan.model.GraveField
import de.hannisoft.graveplan.model.GraveSite
import java.io.File
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class GraveFilesImporter(importDir : String) {
    companion object {
        const val FILE_TOKEN_GRAVE_SITE: String = "Grabstätten_"
        const val FILE_TOKEN_GRAVE: String = "Verstorbene_"
        const val FILE_TOKEN_CRITERIA: String = "Auswahlkriterien_"
        const val FILE_ENDING: String = ".xlsx"
        val format: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val format2: DateFormat = SimpleDateFormat("dd.MM.yyyy")
    }
    val importDir: String = importDir.trimEnd('/') + '/'
    val graveSites = mutableMapOf<String, GraveSite>()
    lateinit var dueDate: String
    lateinit var gravesMap: Map<String, GraveField>

    fun import(dueDateString: String = findNewestTimeString(importDir)): GraveFilesImporter  {
        this.dueDate = dueDateString
        println("Start import from $importDir with timestamp-suffix $dueDate")
        val graveSites = GraveSiteFileReader().read(File(importDir + FILE_TOKEN_GRAVE_SITE + dueDateString + FILE_ENDING))
        gravesMap = GraveFileReader().read(File(importDir + FILE_TOKEN_GRAVE + dueDateString + FILE_ENDING), graveSites)
        GraveSiteCriteriaFileReader().read(File(importDir + FILE_TOKEN_CRITERIA + dueDateString + FILE_ENDING), graveSites)
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

fun parseDateString(dateString: String): Date? {
    if (dateString.isEmpty()) {
        return null
    }
    return try {
        format.parse(dateString)
    } catch (_: ParseException) {
        format2.parse(dateString)
    }
}
