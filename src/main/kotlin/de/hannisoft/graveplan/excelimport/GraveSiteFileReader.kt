package de.hannisoft.de.hannisoft.graveplan.excelimport

import de.hannisoft.graveplan.model.GraveSite
import de.hannisoft.graveplan.model.Owner
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat

class GraveSiteFileReader() {
    companion object {
        const val COL_FIELD: String = "Feld"
        const val COL_ROW: String = "Reihe"
        const val COL_PLACE: String = "Grabstätte"
        const val COL_TYPE: String = "Grabart"
        const val COL_NAME: String = "Grabname"
        const val COL_VALID_FROM: String = "Nutzungsbeginn"
        const val COL_VALID_TO: String = "Nutzungsende"
        const val COL_SIZE: String = "Grabstellenanzahl"
        const val COL_LAST_NAME: String = "Nachname"
        const val COL_FIRST_NAME: String = "Vorname"
        const val COL_STREET: String = "Straße"
        const val COL_BUILDING_NO: String = "Hausnr."
        const val COL_ZIP: String = "PLZ"
        const val COL_CITY: String = "Ort"
        const val COL_SALUTAION: String = "Anrede"
        const val COL_SALUTATION_LETTER: String = "Briefanrede"
    }

    fun read(inputFile: File): Map<String, GraveSite> {
        val graves = mutableMapOf<String, GraveSite>()
        val format: DateFormat = SimpleDateFormat("dd/MM/yyyy")

        inputFile.inputStream().use { fis ->
            val workbook = WorkbookFactory.create(fis)
            val sheet = workbook.getSheetAt(0)
            val headerMap = sheet.buildHeaderMap()

            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                val field = row.getValue(headerMap[COL_FIELD])
                if (field.isEmpty()) {
                    continue
                }

                val rowStr = row.getValue(headerMap[COL_ROW])
                val placeStr = row.getValue(headerMap[COL_PLACE])
                val type = row.getValue(headerMap[COL_TYPE])
                val name = row.getValue(headerMap[COL_NAME])
                val validFrom = row.getValue(headerMap[COL_VALID_FROM])
                val validTo = row.getValue(headerMap[COL_VALID_TO])
                val size = row.getValue(headerMap[COL_SIZE])

                val owner = Owner(row.getValue(headerMap[COL_SALUTAION]),
                    row.getValue(headerMap[COL_SALUTATION_LETTER]),
                    row.getValue(headerMap[COL_FIRST_NAME]),
                    row.getValue(headerMap[COL_LAST_NAME]),
                    row.getValue(headerMap[COL_STREET])+ " " + row.getValue(headerMap[COL_BUILDING_NO]),
                    row.getValue(headerMap[COL_ZIP])+ " " + row.getValue(headerMap[COL_CITY]))
                val graveSite = GraveSite(field, rowStr, placeStr)
                graveSite.typeAsString = type
                graveSite.owner = owner
                graveSite.name = name
                if (validFrom.isNotEmpty()) {
                    graveSite.validFrom = format.parse(validFrom)
                }
                if (validTo.isNotEmpty()) {
                    graveSite.validTo = format.parse(validTo)
                }
                graveSite.size = size.toInt()
                graves[graveSite.id] = graveSite
            }
        }
        return graves
    }
}