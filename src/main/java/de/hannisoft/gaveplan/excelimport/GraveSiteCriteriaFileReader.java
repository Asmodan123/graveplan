package de.hannisoft.graveplan.excelimport;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import de.hannisoft.graveplan.model.GraveSite;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class GraveSiteCriteriaFileReader extends AbstractXLSFileReader {
    private static final String COL_FIELD = "Feld";
    private static final String COL_ROW = "Reihe";
    private static final String COL_PLACE = "Grabstätte";
    private static final String COL_CRIT_1 = "Auswahlkriterium 1";
    private static final String COL_CRIT_2 = "Auswahlkriterium 2";
    private static final String COL_CRIT_3 = "Auswahlkriterium 3";

    public void read(String inputFile, Map<String, GraveSite> graves) throws IOException {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("Cp1252");
        try {
            w = Workbook.getWorkbook(inputWorkbook, ws);
            Sheet sheet = w.getSheet(0);
            initColumnMap(sheet, 0);
            for (int i = 2; i < sheet.getRows(); i++) {
                String crit1 = getContent(COL_CRIT_1, i);
                String crit2 = getContent(COL_CRIT_2, i);
                String crit3 = getContent(COL_CRIT_3, i);

                if ((crit1 == null || crit1.trim().isEmpty()) && (crit2 == null || crit2.trim().isEmpty())
                        && (crit3 == null || crit3.trim().isEmpty())) {
                    continue;
                }

                String field = getContent(COL_FIELD, i);
                String row = getContent(COL_ROW, i);
                String place = getContent(COL_PLACE, i);
                String graveId = GraveSite.createId(field, row, place);
                GraveSite grave = graves.get(graveId);
                if (grave != null) {
                    grave.addCriteria(crit1);
                    grave.addCriteria(crit2);
                    grave.addCriteria(crit3);

                }
            }
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

}
