package de.hannisoft.gaveplan.excelimport;

import java.util.HashMap;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;

public abstract class AbstractXMLFileReader {
    private Map<String, Integer> columnIndex = null;
    private Sheet sheet = null;

    protected void initColumnMap(Sheet sheet, int headerRowIdx) {
        this.sheet = sheet;
        columnIndex = new HashMap<>();
        Cell[] headerRow = sheet.getRow(headerRowIdx);
        for (int i = 0; i < headerRow.length; i++) {
            String content = headerRow[i].getContents();
            if (content == null || content.trim().isEmpty()) {
                break;
            }
            columnIndex.put(content, i);
        }
    }

    protected String getContent(String columnName, int row) {
        Integer colIdx = columnIndex.get(columnName);
        if (colIdx == null) {
            throw new RuntimeException("Column " + columnName + " not found");
        }
        return sheet.getCell(colIdx, row).getContents();
    }
}
