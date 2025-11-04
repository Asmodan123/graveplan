package de.hannisoft.graveplan.excelimport

import de.hannisoft.de.hannisoft.graveplan.excelimport.buildHeaderMap
import de.hannisoft.de.hannisoft.graveplan.excelimport.getValue
import de.hannisoft.de.hannisoft.graveplan.excelimport.parseDateString
import de.hannisoft.graveplan.model.Grave
import de.hannisoft.graveplan.model.GraveMap
import de.hannisoft.graveplan.model.GraveSite
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

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

    fun read(inputFile: File, graves: Map<String, GraveSite>) {
        val emptyGraveSites = graves.toMutableMap()
        val gravesMaps = mutableMapOf<String, GraveMap>()

        inputFile.inputStream().use { fis ->
            val workbook = WorkbookFactory.create(fis)
            val sheet = workbook.getSheetAt(0)
            var headerMap = sheet.buildHeaderMap()
            var startRow = 1
            if (headerMap.size < 5) {
                // if the header map contains less than 5 columns, there is a "GroupingRow" above the current column header row, which must be ignored
                headerMap = sheet.buildHeaderMap(1)
                startRow = 2
            }
            for (rowIndex in startRow..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue
                val graveId = row.getValue(headerMap[COL_GARVE_ID])
                try {
                    emptyGraveSites.remove(graveId)
                    graves[graveId]?.apply {
                        val graveString = row.getValue(headerMap[COL_PLACE]).split("/")
                        val rowStr = graveString[0].trim()
                        val placeStr = graveString[1].trim().substring(0, 2)
                        val lastName = row.getValue(headerMap[COL_LAST_NAME])
                        val firstName = row.getValue(headerMap[COL_FIRST_NAME])
                        var dateOfDeath = row.getValue(headerMap[COL_DOD])
                        if (dateOfDeath.isEmpty()) {
                            dateOfDeath = row.getValue(headerMap[COL_FUNERAL_DATE])
                        }
                        val dateOfBirth = row.getValue(headerMap[COL_DOB])
                        val grave = Grave(this, rowStr.toInt(), placeStr.toInt())
                        grave.deceased = "$firstName $lastName"
                        grave.dateOfDeath = parseDateString(dateOfDeath)
                        grave.dateOfBirth = parseDateString(dateOfBirth)
                        this.graves.add(grave)

                        val field = this.field
                        val placeMap = gravesMaps[field] ?: GraveMap(field).also {
                            gravesMaps[field] = it
                        }
                        placeMap.addGraveSite(this)
                    }
                } catch (e: Exception) {
                    System.err.println("${e.javaClass.simpleName} while reading grave $graveId from $inputFile at row $rowIndex: ${e.message}")
                    throw e
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