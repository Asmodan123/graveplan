package de.hannisoft.de.hannisoft.graveplan.excelimport

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet


fun Sheet.buildHeaderMap(headerRowIndex: Int = 0): Map<String, Int> {
    val headerRow = this.getRow(headerRowIndex)
        ?: throw IllegalArgumentException("Header row $headerRowIndex does not exist")
    return headerRow.fold(mutableMapOf()) {
        headerMap, cell ->
            headerMap.putIfAbsent(cell.stringCellValue, cell.columnIndex)
            headerMap
    }
}

fun Row.getValue(index: Int?): String {
    return (this.getCell(index ?: -1) ?: "").toString()
}