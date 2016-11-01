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

public class PlaceFileReader {
    public Map<String, PlaceMap> read(String inputFile, Map<String, Grave> graves) throws IOException {
        Map<String, PlaceMap> placeMaps = new HashMap<>();
        File inputWorkbook = new File(inputFile);
        Workbook w;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("Cp1252");
        try {
            w = Workbook.getWorkbook(inputWorkbook, ws);
            Sheet sheet = w.getSheet(0);

            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            for (int i = 2; i < sheet.getRows(); i++) {
                String graveId = sheet.getCell(4, i).getContents();
                Grave grave = graves.get(graveId);
                if (grave != null) {
                    String[] placeString = sheet.getCell(5, i).getContents().split("\\/");
                    String row = placeString[0];
                    String placeNo = placeString[1];
                    String lastName = sheet.getCell(1, i).getContents();
                    String firstName = sheet.getCell(2, i).getContents();
                    String dateOfDeath = sheet.getCell(7, i).getContents();

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
