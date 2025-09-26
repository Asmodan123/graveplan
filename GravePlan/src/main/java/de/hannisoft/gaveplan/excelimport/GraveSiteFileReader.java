package de.hannisoft.gaveplan.excelimport;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

import de.hannisoft.gaveplan.model.GraveSite;
import de.hannisoft.gaveplan.model.GraveSiteType;
import de.hannisoft.gaveplan.model.Owner;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class GraveSiteFileReader extends AbstractXLSFileReader {
    private static final String COL_FIELD = "Feld";
    private static final String COL_ROW = "Reihe";
    private static final String COL_PLACE = "Grabstätte";
    private static final String COL_TYPE = "Grabart";
    private static final String COL_NAME = "Grabname";
    private static final String COL_VALID_FROM = "Nutzungsbeginn";
    private static final String COL_VALID_TO = "Nutzungsende";
    private static final String COL_SIZE = "Grabstellenanzahl";
    private static final String COL_LAST_NAME = "Nachname";
    private static final String COL_FIRST_NAME = "Vorname";
    private static final String COL_STREET = "Straße";
    private static final String COL_BUILDING_NO = "Hausnr.";
    private static final String COL_ZIP = "PLZ";
    private static final String COL_CITY = "Ort";
    private static final String COL_SALUTAION = "Anrede";
    private static final String COL_SALUTATION_LETTER = "Briefanrede";

    public Map<String, GraveSite> read(String inputFile) throws IOException {
        Map<String, GraveSite> graves = new TreeMap<>();
        File inputWorkbook = new File(inputFile);
        Workbook w;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("Cp1252");
        try {
            w = Workbook.getWorkbook(inputWorkbook, ws);
            Sheet sheet = w.getSheet(0);
            initColumnMap(sheet, 0);

            // DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            for (int i = 1; i < sheet.getRows(); i++) {
                String field = getContent(COL_FIELD, i);
                if (field == null || field.trim().isEmpty()) {
                    continue;
                }
                String row = getContent(COL_ROW, i);
                String place = getContent(COL_PLACE, i);
                String type = getContent(COL_TYPE, i);
                String name = getContent(COL_NAME, i);
                String validFrom = getContent(COL_VALID_FROM, i);
                String validTo = getContent(COL_VALID_TO, i);
                String size = getContent(COL_SIZE, i);

                Owner owner = new Owner();
                owner.setLastName(getContent(COL_LAST_NAME, i));
                owner.setFirstName(getContent(COL_FIRST_NAME, i));
                owner.setStreet(getContent(COL_STREET, i) + " " + getContent(COL_BUILDING_NO, i));
                owner.setZipAndTown(getContent(COL_ZIP, i) + " " + getContent(COL_CITY, i));
                owner.setSaluation(getContent(COL_SALUTAION, i));
                owner.setSaluationLetter(getContent(COL_SALUTATION_LETTER, i));

                try {
                    GraveSite graveSite = new GraveSite(field, row, place);
                    graveSite.setType(GraveSiteType.getTypeByName(type));
                    graveSite.setOwner(owner);
                    graveSite.setName(name);
                    if (validFrom != "") {
                        graveSite.setValidFrom(format.parse(validFrom));
                    }
                    if (validTo != "") {
                        graveSite.setValidTo(format.parse(validTo));
                    }
                    graveSite.setSize(Integer.parseInt(size));
                    graves.put(graveSite.getId(), graveSite);
                } catch (Exception e) {
                    System.err.println("Invalid GraveSite: " + field + "/" + row + "/" + place);
                    e.printStackTrace();
                }
            }
        } catch (BiffException e) {
            e.printStackTrace();
            return null;
        }
        return graves;
    }

}
