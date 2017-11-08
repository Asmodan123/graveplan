package de.hannisoft.gaveplan.excelimport;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import de.hannisoft.gaveplan.model.Grave;
import de.hannisoft.gaveplan.model.Place;
import de.hannisoft.gaveplan.model.PlaceMap;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class PlaceFileReader extends AbstractXLSFileReader {
    private static final String COL_GARVE_ID = "Grabstätte";
    private static final String COL_PLACE = "Grabstelle";
    private static final String COL_LAST_NAME = "Nachname";
    private static final String COL_FIRST_NAME = "Vorname";
    private static final String COL_DOD = "Sterbedatum";

    public Map<String, PlaceMap> read(String inputFile, Map<String, Grave> graves) throws IOException {
        Map<String, PlaceMap> placeMaps = new HashMap<>();
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
                Grave grave = graves.get(graveId);
                if (grave != null) {
                    String[] placeString = getContent(COL_PLACE, i).split("\\/");
                    String row = placeString[0];
                    String placeNo = placeString[1];
                    String lastName = getContent(COL_LAST_NAME, i);
                    String firstName = getContent(COL_FIRST_NAME, i);
                    String dateOfDeath = getContent(COL_DOD, i);

                    try {
                        Place place = new Place(grave, row, placeNo);
                        place.setDeceased(firstName + " " + lastName);
                        if (dateOfDeath != null && !dateOfDeath.trim().isEmpty()) {
                            place.setDateOfDeatch(format.parse(dateOfDeath));
                        }
                        grave.getPlaces().add(place);

                        String field = grave.getField();
                        PlaceMap placeMap = placeMaps.get(field);
                        if (placeMap == null) {
                            placeMap = new PlaceMap(field);
                            placeMaps.put(field, placeMap);
                        }
                        placeMap.addGrave(grave);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (BiffException e) {
            e.printStackTrace();
        }
        return placeMaps;
    }

}
