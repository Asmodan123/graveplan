package de.hannisoft.gaveplan;

import java.util.Map;

import de.hannisoft.gaveplan.excelimport.GraveCriteriaFileReader;
import de.hannisoft.gaveplan.excelimport.GraveFileReader;
import de.hannisoft.gaveplan.excelimport.PlaceFileReader;
import de.hannisoft.gaveplan.export.PlaceMapWriter;
import de.hannisoft.gaveplan.export.ZipCreator;
import de.hannisoft.gaveplan.model.Grave;
import de.hannisoft.gaveplan.model.PlaceMap;

public class GravePlan {
    public static final String FILE_TOKEN_GRAVE = "Grabstätten_";
    public static final String FILE_TOKEN_PLACE = "Verstorbene_";
    public static final String FILE_TOKEN_CRITERIA = "Auswahlkriterien_";
    public static final String FILE_ENDING = ".xls";

    public void run(String importDir, String outputDir, String timestamp) throws Exception {
        String graveFile = importDir + FILE_TOKEN_GRAVE + timestamp + FILE_ENDING;
        String placeFile = importDir + FILE_TOKEN_PLACE + timestamp + FILE_ENDING;
        String criteriaFile = importDir + FILE_TOKEN_CRITERIA + timestamp + FILE_ENDING;

        GraveFileReader graveReader = new GraveFileReader();
        Map<String, Grave> graves = graveReader.read(graveFile);

        PlaceFileReader placeReader = new PlaceFileReader();
        Map<String, PlaceMap> placeMaps = placeReader.read(placeFile, graves);

        GraveCriteriaFileReader criteruaReader = new GraveCriteriaFileReader();
        criteruaReader.read(criteriaFile, graves);

        String dueDay = timestamp.substring(6, 8) + "." + timestamp.substring(4, 6) + "." + timestamp.substring(0, 4);
        PlaceMapWriter writer = new PlaceMapWriter();
        writer.write(outputDir, placeMaps, dueDay);
        //
        // PNGPlaceMapWriter pngWriter = new PNGPlaceMapWriter();
        // pngWriter.drawDefaultMap();
        //
        ZipCreator zipper = new ZipCreator();
        zipper.zipFolder(outputDir, outputDir + "Lageplan_" + timestamp + ".zip");
    }

    public static void main(String[] args) throws Exception {
        String importDir = "/home/johannes/Dokumente/Friedhof/export/";
        String outputDir = "/home/johannes/tmp/plan/";
        String timestamp = "20171127";
        if (args != null && args.length == 3) {
            importDir = args[0];
            outputDir = args[1];
            timestamp = args[2];
        }
        GravePlan gravePlane = new GravePlan();
        gravePlane.run(importDir, outputDir, timestamp);
    }

}
