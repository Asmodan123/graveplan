package de.hannisoft.graveplan.excelimport

import de.hannisoft.de.hannisoft.graveplan.excelimport.buildHeaderMap
import de.hannisoft.de.hannisoft.graveplan.excelimport.getValue
import de.hannisoft.graveplan.model.Grave
import de.hannisoft.graveplan.model.GraveMap
import de.hannisoft.graveplan.model.GraveSite
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat

class GraveFileReader {
    companion object {
        const val COL_GARVE_ID: String = "Grabstätte"
        const val COL_PLACE: String = "Grabstelle"
        const val COL_LAST_NAME: String = "Nachname"
        const val COL_FIRST_NAME: String = "Vorname"
        const val COL_DOD: String = "Sterbedatum"
        const val COL_DOB: String = "Geburtsdatum"
        const val COL_FUNERAL_DATE: String = "Bestattungsdatum"
    }

    fun read(inputFile: String, graves: Map<String, GraveSite>) {
        val emptyGraveSites = graves.toMutableMap()
        val gravesMaps = mutableMapOf<String, GraveMap>()
        val sourceFile = File(inputFile)
        val format: DateFormat = SimpleDateFormat("dd.MM.yyyy")

        sourceFile.inputStream().use { fis ->
            val workbook = WorkbookFactory.create(fis)
            val sheet = workbook.getSheetAt(0)
            val headerMap = sheet.buildHeaderMap()
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue
                val graveId = row.getValue(headerMap[COL_GARVE_ID])
                emptyGraveSites.remove(graveId)
                graves[graveId]?.apply {
                    val graveString = row.getValue(headerMap[COL_PLACE]).split("/")
                    val rowStr = graveString[0].trim()
                    val placeStr = graveString[1].trim()
                    val lastName = row.getValue(headerMap[COL_LAST_NAME])
                    val firstName = row.getValue(headerMap[COL_FIRST_NAME])
                    var dateOfDeatch = row.getValue(headerMap[COL_DOD])
                    if (dateOfDeatch.isEmpty()) {
                        dateOfDeatch = row.getValue(headerMap[COL_FUNERAL_DATE])
                    }
                    val dateOfBirth = row.getValue(headerMap[COL_DOB])
                    val grave = Grave(this, rowStr.toInt(), placeStr.toInt())
                    grave.deceased = "$firstName $lastName"
                    if (dateOfDeatch.isNotEmpty()) {
                        grave.dateOfDeath = format.parse(dateOfDeatch)
                    }
                    if (dateOfBirth.isNotEmpty()) {
                        grave.dateOfBirth = format.parse(dateOfBirth)
                    }
                    this.graves.add(grave)

                    val field = this.field
                    val placeMap = gravesMaps[field] ?: GraveMap(field).also {
                        gravesMaps[field] = it
                    }
                    placeMap.addGraveSite(this)
                }
            }
        }

        emptyGraveSites.values.forEach {
            val field = it.field
            val placeMap = gravesMaps[field] ?: GraveMap(field).also {
                gravesMaps[field] = it
            }
            placeMap.addGraveSite(it)
        }
    }
}