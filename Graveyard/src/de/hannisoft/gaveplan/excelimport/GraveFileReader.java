package de.hannisoft.gaveplan.excelimport;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import de.hannisoft.gaveplan.model.Grave;
import de.hannisoft.gaveplan.model.GraveType;
import de.hannisoft.gaveplan.model.Owner;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class GraveFileReader {
    public Map<String, Grave> read(String inputFile) throws IOException {
        Map<String, Grave> graves = new HashMap<>();
        File inputWorkbook = new File(inputFile);
        Workbook w;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("Cp1252");
        try {
            w = Workbook.getWorkbook(inputWorkbook, ws);
            Sheet sheet = w.getSheet(0);

            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            for (int i = 1; i < sheet.getRows(); i++) {
                String field = sheet.getCell(1, i).getContents();
                if (field == null || field.trim().isEmpty()) {
                    continue;
                }
                String row = sheet.getCell(2, i).getContents();
                String place = sheet.getCell(3, i).getContents();
                String type = sheet.getCell(4, i).getContents();
                String name = sheet.getCell(5, i).getContents();
                String validFrom = sheet.getCell(6, i).getContents();
                String validTo = sheet.getCell(7, i).getContents();
                String size = sheet.getCell(17, i).getContents();

                Owner owner = new Owner();
                owner.setLastName(sheet.getCell(9, i).getContents());
                owner.setFirstName(sheet.getCell(10, i).getContents());
                owner.setStreet(sheet.getCell(11, i).getContents() + " " + sheet.getCell(12, i).getContents());
                owner.setZipAndTown(sheet.getCell(13, i).getContents() + " " + sheet.getCell(14, i).getContents());
                owner.setSaluation(sheet.getCell(15, i).getContents());
                owner.setSaluationLetter(sheet.getCell(16, i).getContents());

                try {
                    Grave grave = new Grave(field, row, place);
                    grave.setType(GraveType.getTypeByName(type));
                    grave.setOwner(owner);
                    grave.setName(name);
                    if (validFrom != "") {
                        grave.setValidFrom(format.parse(validFrom));
                    }
                    if (validTo != "") {
                        grave.setValidTo(format.parse(validTo));
                    }
                    grave.setSize(Integer.parseInt(size));
                    graves.put(grave.getId(), grave);
                } catch (Exception e) {
                    System.err.println("Invalid Grave: " + field + "/" + row + "/" + place);
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
