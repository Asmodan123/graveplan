package de.hannisoft.gaveplan;

import java.util.Map;

import de.hannisoft.gaveplan.excelimport.GraveFileReader;
import de.hannisoft.gaveplan.excelimport.PlaceFileReader;
import de.hannisoft.gaveplan.export.PlaceMapWriter;
import de.hannisoft.gaveplan.model.Grave;
import de.hannisoft.gaveplan.model.PlaceMap;

public class GravePlan {
    public void run(String graveFile, String placeFile, String outputDir, String dueDay) throws Exception {
        GraveFileReader graveReader = new GraveFileReader();
        Map<String, Grave> graves = graveReader.read(graveFile);

        PlaceFileReader placeReader = new PlaceFileReader();
        Map<String, PlaceMap> placeMaps = placeReader.read(placeFile, graves);

        PlaceMapWriter writer = new PlaceMapWriter();
        writer.write(outputDir, placeMaps, dueDay);
    }

    public static void main(String[] args) throws Exception {
        String timestamp = "20170327";
        String importDir = "/home/johannes/Dokumente/Friedhof/export/";
        String graveFile = importDir + "Grabstätten_" + timestamp + ".xls";
        String placeFile = importDir + "Verstorbene_" + timestamp + ".xls";
        String outputDir = "/home/johannes/tmp/plan/";
        if (args != null && args.length == 3) {
            graveFile = args[0];
            placeFile = args[1];
            outputDir = args[2];
        }
        String dueDay = timestamp.substring(6, 8) + "." + timestamp.substring(4, 6) + "." + timestamp.substring(0, 4);
        GravePlan gravePlane = new GravePlan();
        gravePlane.run(graveFile, placeFile, outputDir, dueDay);
    }

}
