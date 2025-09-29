package de.hannisoft.de.hannisoft.graveplan.excelimport

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet


fun Sheet.buildHeaderMap(headerRowIndex: Int = 0): Map<String, Int> {
    val headerRow = this.getRow(headerRowIndex)
        ?: throw IllegalArgumentException("Header row $headerRowIndex does not exist")

    return headerRow.mapNotNull { cell ->
        val key = cell.stringCellValue?.trim()
        if (!key.isNullOrEmpty()) key to cell.columnIndex else null
    }.toMap()
}

fun Row.getValue(index: Int?): String {
    return (this.getCell(index ?: -1) ?: "").toString()
}