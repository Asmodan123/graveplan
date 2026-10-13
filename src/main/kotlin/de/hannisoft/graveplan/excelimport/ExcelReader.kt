package de.hannisoft.graveplan.excelimport

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import java.util.Date

// HadesX exports start with a title row ("Auswertung nach ..."), followed by the column header row
const val HEADER_ROW: Int = 1
const val FIRST_DATA_ROW: Int = 2

// HadesX exports this date (Excel day 0) as placeholder for an empty date
private const val EMPTY_DATE_PLACEHOLDER: String = "30.12.1899"

fun Sheet.buildHeaderMap(headerRowIndex: Int = HEADER_ROW): Map<String, Int> {
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

fun Row.getDate(index: Int?): Date? {
    val cell = this.getCell(index ?: -1) ?: return null
    if (cell.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
        return cell.dateCellValue
    }
    // dates before 1900 can't be stored as Excel dates, so HadesX exports them as string
    val dateString = cell.toString().trim()
    if (dateString == EMPTY_DATE_PLACEHOLDER) {
        return null
    }
    return parseDateString(dateString)
}
