package de.hannisoft.graveplan.excelimport

import de.hannisoft.graveplan.model.GraveSite
import de.hannisoft.graveplan.model.Owner
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

class GraveSiteFileReader() {
    companion object {
        const val COL_FIELD: String = "Einteilung 1"
        const val COL_ROW: String = "Einteilung 2"
        const val COL_PLACE: String = "Grabstätte"
        const val COL_TYPE: String = "Grabart"
        const val COL_NAME: String = "Grabname"
        const val COL_CRITERIA: String = "Auswahlkriterien"
        const val COL_VALID_FROM: String = "Nutzungsbeginn"
        const val COL_VALID_TO: String = "Nutzungsende"
        const val COL_GRAVES: String = "Grabstellen"
        const val COL_LAST_NAME: String = "Name (NB)"
        const val COL_FIRST_NAME: String = "Vorname (NB)"
        const val COL_STREET: String = "Strasse (NB)"
        const val COL_BUILDING_NO: String = "Hausnr (NB)"
        const val COL_ZIP: String = "PLZ (NB)"
        const val COL_CITY: String = "Ort (NB)"
    }

    fun read(inputFile: File): Map<String, GraveSite> {
        val graves = mutableMapOf<String, GraveSite>()

        inputFile.inputStream().use { fis ->
            val workbook = WorkbookFactory.create(fis)
            val sheet = workbook.getSheetAt(0)
            val headerMap = sheet.buildHeaderMap()

            for (rowIndex in FIRST_DATA_ROW..sheet.lastRowNum) {
                try {
                    val row = sheet.getRow(rowIndex) ?: continue

                    val field = row.getValue(headerMap[COL_FIELD])
                    if (field.isEmpty()) {
                        continue
                    }

                    val rowStr = row.getValue(headerMap[COL_ROW])
                    val placeStr = row.getValue(headerMap[COL_PLACE])
                    val type = row.getValue(headerMap[COL_TYPE])
                    val name = row.getValue(headerMap[COL_NAME])
                    // e.g. "01/01, 01/02"
                    val graveCount = row.getValue(headerMap[COL_GRAVES]).split(",").count { it.isNotBlank() }

                    val owner = Owner(null,
                        null,
                        row.getValue(headerMap[COL_FIRST_NAME]),
                        row.getValue(headerMap[COL_LAST_NAME]),
                        row.getValue(headerMap[COL_STREET])+ " " + row.getValue(headerMap[COL_BUILDING_NO]),
                        row.getValue(headerMap[COL_ZIP])+ " " + row.getValue(headerMap[COL_CITY]))
                    val graveSite = GraveSite(field, rowStr, placeStr)
                    graveSite.typeAsString = type
                    graveSite.owner = owner
                    graveSite.name = name
                    graveSite.validFrom = row.getDate(headerMap[COL_VALID_FROM])
                    graveSite.validTo = row.getDate(headerMap[COL_VALID_TO])
                    graveSite.size = graveCount
                    // e.g. "abgeräumt, gesperrt"
                    row.getValue(headerMap[COL_CRITERIA]).split(",").forEach { graveSite.addCriteria(it.trim()) }
                    graves[graveSite.id] = graveSite
                } catch (e: Exception) {
                    System.err.println("${e.javaClass.simpleName} while reading grave site from $inputFile at row $rowIndex: ${e.message}")
                    throw e
                }
            }
        }
        return graves
    }
}
