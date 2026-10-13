package de.hannisoft.graveplan.excelimport

import de.hannisoft.graveplan.model.Grave
import de.hannisoft.graveplan.model.GraveField
import de.hannisoft.graveplan.model.GraveSite
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

class GraveFileReader {
    companion object {
        const val COL_FIELD: String = "Abteilung"
        const val COL_ROW: String = "Block"
        const val COL_GRAVE_SITE_PLACE: String = "Grab"
        const val COL_PLACE: String = "Grabstelle"
        const val COL_LAST_NAME: String = "Name (VS)"
        const val COL_FIRST_NAME: String = "Vorname (VS)"
        const val COL_DOD: String = "Sterbedatum (VS)"
        const val COL_DOB: String = "Geburtsdatum (VS)"
        const val COL_FUNERAL_DATE: String = "Bestattungsdatum"

        private val REGULAR_PLACE = Regex("""-?\d+/-?\d+""")
        // last "<row>/<place>" pair, optionally followed by non-digits like "b"
        private val PLACE = Regex("""(-?\d+)\s*/\s*(-?\d+)\D*$""")
        private val PLACE_SUFFIX = Regex("""\(.*\)""")
    }

    fun read(inputFile: File, graves: Map<String, GraveSite>): Map<String, GraveField> {
        val emptyGraveSites = graves.toMutableMap()
        val gravesMaps = mutableMapOf<String, GraveField>()

        inputFile.inputStream().use { fis ->
            val workbook = WorkbookFactory.create(fis)
            val sheet = workbook.getSheetAt(0)
            val headerMap = sheet.buildHeaderMap()
            for (rowIndex in FIRST_DATA_ROW..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue
                val graveId = GraveSite.createId(
                    row.getValue(headerMap[COL_FIELD]),
                    row.getValue(headerMap[COL_ROW]),
                    row.getValue(headerMap[COL_GRAVE_SITE_PLACE]))
                if (graveId == "//") {
                    continue
                }
                try {
                    emptyGraveSites.remove(graveId)
                    val graveSite = graves[graveId]
                    if (graveSite == null) {
                        System.err.println("No grave site $graveId found for row $rowIndex of $inputFile")
                        continue
                    }
                    graveSite.apply {
                        val placeString = row.getValue(headerMap[COL_PLACE])
                        val (rowNo, placeNo) = parsePlace(placeString) ?: run {
                            System.err.println("Invalid Grabstelle '$placeString' of grave site $graveId at row $rowIndex, skipping")
                            return@apply
                        }
                        if (!REGULAR_PLACE.matches(placeString)) {
                            println("Irregular Grabstelle '$placeString' of grave site $graveId read as $rowNo/$placeNo")
                        }
                        val lastName = row.getValue(headerMap[COL_LAST_NAME])
                        val firstName = row.getValue(headerMap[COL_FIRST_NAME])
                        val grave = Grave(this, rowNo, placeNo, "$firstName $lastName".trim())
                        grave.dateOfDeath = row.getDate(headerMap[COL_DOD]) ?: row.getDate(headerMap[COL_FUNERAL_DATE])
                        grave.dateOfBirth = row.getDate(headerMap[COL_DOB])
                        this.graves.add(grave)

                        val field = this.field
                        val placeMap = gravesMaps[field] ?: GraveField(field).also {
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

        emptyGraveSites.values.forEach { graveSite ->
            val field = graveSite.field
            val placeMap = gravesMaps[field] ?:
                GraveField(field).also { graveField ->
                    gravesMaps[field] = graveField
                }
            placeMap.addGraveSite(graveSite)
        }
        return gravesMaps
    }

    // e.g. "01/09", "04 / 01", "17/-02", "03/02 (E3)", "14/02b", "Q3/12/12"
    private fun parsePlace(placeString: String): Pair<Int, Int>? =
        PLACE.find(placeString.replace(PLACE_SUFFIX, "").trim())?.let {
            it.groupValues[1].toInt() to it.groupValues[2].toInt()
        }
}
