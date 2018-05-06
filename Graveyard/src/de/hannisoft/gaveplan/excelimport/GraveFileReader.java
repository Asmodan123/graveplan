package de.hannisoft.gaveplan.excelimport;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import de.hannisoft.gaveplan.model.Grave;
import de.hannisoft.gaveplan.model.GraveMap;
import de.hannisoft.gaveplan.model.GraveSite;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class GraveFileReader extends AbstractXLSFileReader {
    private static final String COL_GARVE_ID = "Grabstätte";
    private static final String COL_PLACE = "Grabstelle";
    private static final String COL_LAST_NAME = "Nachname";
    private static final String COL_FIRST_NAME = "Vorname";
    private static final String COL_DOD = "Sterbedatum";

    public Map<String, GraveMap> read(String inputFile, final Map<String, GraveSite> graveSites) throws IOException {
        Map<String, GraveSite> sites = new HashMap<String, GraveSite>(graveSites);
        Map<String, GraveMap> graveMaps = new HashMap<>();
        File inputWorkbook = new File(inputFile);
        Workbook w;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("Cp1252");
        try {
            w = Workbook.getWorkbook(inputWorkbook, ws);
            Sheet sheet = w.getSheet(0);
            initColumnMap(sheet, 1);
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            for (int i = 2; i < sheet.getRows(); i++) {
                String graveId = getContent(COL_GARVE_ID, i);
                GraveSite graveSite = sites.remove(graveId);
                if (graveSite != null) {
                    String[] graveString = getContent(COL_PLACE, i).split("\\/");
                    String row = graveString[0];
                    String placeNo = graveString[1];
                    String lastName = getContent(COL_LAST_NAME, i);
                    String firstName = getContent(COL_FIRST_NAME, i);
                    String dateOfDeath = getContent(COL_DOD, i);

                    try {
                        Grave grave = new Grave(graveSite, row, placeNo);
                        grave.setDeceased(firstName + " " + lastName);
                        if (dateOfDeath != null && !dateOfDeath.trim().isEmpty()) {
                            grave.setDateOfDeatch(format.parse(dateOfDeath));
                        }
                        graveSite.getGraves().add(grave);

                        String field = graveSite.getField();
                        GraveMap placeMap = graveMaps.get(field);
                        if (placeMap == null) {
                            placeMap = new GraveMap(field);
                            graveMaps.put(field, placeMap);
                        }
                        placeMap.addGraveSite(graveSite);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (BiffException e) {
            e.printStackTrace();
        }
        for (GraveSite graveSite : sites.values()) {
            String field = graveSite.getField();
            GraveMap placeMap = graveMaps.get(field);
            if (placeMap == null) {
                placeMap = new GraveMap(field);
                graveMaps.put(field, placeMap);
            }
            placeMap.addGraveSite(graveSite);
        }
        return graveMaps;
    }

}
