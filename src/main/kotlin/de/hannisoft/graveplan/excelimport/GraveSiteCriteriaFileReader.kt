package de.hannisoft.graveplan.excelimport

import de.hannisoft.de.hannisoft.graveplan.excelimport.buildHeaderMap
import de.hannisoft.de.hannisoft.graveplan.excelimport.getValue
import de.hannisoft.graveplan.model.GraveSite
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

class GraveSiteCriteriaFileReader {
    companion object {
        const val COL_FIELD: String = "Feld"
        const val COL_ROW: String = "Reihe"
        const val COL_PLACE: String = "Grabstätte"
        const val COL_CRIT_1: String = "Auswahlkriterium 1"
        const val COL_CRIT_2: String = "Auswahlkriterium 2"
        const val COL_CRIT_3: String = "Auswahlkriterium 3"
    }

    fun read(inputFile: String, graves: Map<String, GraveSite>) {
        val sourceFile = File(inputFile)

        sourceFile.inputStream().use { fis ->
            val workbook = WorkbookFactory.create(fis)
            val sheet = workbook.getSheetAt(0)
            val headerMap = sheet.buildHeaderMap()
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue
                val crit1 = row.getValue(headerMap[COL_CRIT_1])
                val crit2 = row.getValue(headerMap[COL_CRIT_2])
                val crit3 = row.getValue(headerMap[COL_CRIT_3])
                if (crit1.isEmpty() && crit2.isEmpty() && crit3.isEmpty()) {
                    continue
                }

                val field = row.getValue(headerMap[COL_FIELD])
                val rowStr = row.getValue(headerMap[COL_ROW])
                val placeStr = row.getValue(headerMap[COL_PLACE])
                val graveId = GraveSite.createId(field, rowStr, placeStr)
                graves[graveId]?.apply {
                    addCriteria(crit1)
                    addCriteria(crit2)
                    addCriteria(crit3)
                }
            }
        }
    }
}