package de.hannisoft.gaveplan;

import java.util.Map;

import de.hannisoft.gaveplan.excelimport.GraveFileReader;
import de.hannisoft.gaveplan.excelimport.GraveSiteCriteriaFileReader;
import de.hannisoft.gaveplan.excelimport.GraveSiteFileReader;
import de.hannisoft.gaveplan.export.GraveMapWriter;
import de.hannisoft.gaveplan.export.ZipCreator;
import de.hannisoft.gaveplan.model.GraveMap;
import de.hannisoft.gaveplan.model.GraveSite;

public class GravePlan {
    public static final String FILE_TOKEN_GRAVE_SITE = "Grabstätten_";
    public static final String FILE_TOKEN_GRAVE = "Verstorbene_";
    public static final String FILE_TOKEN_CRITERIA = "Auswahlkriterien_";
    public static final String FILE_ENDING = ".xls";

    public void run(String importDir, String outputDir, String timestamp) throws Exception {
        String graveSiteFile = importDir + FILE_TOKEN_GRAVE_SITE + timestamp + FILE_ENDING;
        String graveFile = importDir + FILE_TOKEN_GRAVE + timestamp + FILE_ENDING;
        String criteriaFile = importDir + FILE_TOKEN_CRITERIA + timestamp + FILE_ENDING;

        GraveSiteFileReader graveSiteReader = new GraveSiteFileReader();
        Map<String, GraveSite> graveSites = graveSiteReader.read(graveSiteFile);

        GraveFileReader gaveReader = new GraveFileReader();
        Map<String, GraveMap> graveMaps = gaveReader.read(graveFile, graveSites);

        GraveSiteCriteriaFileReader graveCriteriaReader = new GraveSiteCriteriaFileReader();
        graveCriteriaReader.read(criteriaFile, graveSites);

        String dueDay = timestamp.substring(6, 8) + "." + timestamp.substring(4, 6) + "." + timestamp.substring(0, 4);
        GraveMapWriter writer = new GraveMapWriter();
        writer.write(outputDir, graveMaps, dueDay);
        //
        // PNGGraveMapWriter pngWriter = new PNGGraveMapWriter();
        // pngWriter.drawDefaultMap();
        //
        ZipCreator zipper = new ZipCreator();
        zipper.zipFolder(outputDir, outputDir + "Lageplan_" + timestamp + ".zip");
    }

    public static void main(String[] args) throws Exception {
        String importDir = "/home/johannes/Dokumente/Friedhof/export/";
        String outputDir = "/home/johannes/tmp/plan2/";
        String timestamp = "20180523";
        if (args != null && args.length == 3) {
            importDir = args[0];
            outputDir = args[1];
            timestamp = args[2];
        }
        GravePlan gravePlane = new GravePlan();
        gravePlane.run(importDir, outputDir, timestamp);
    }

}
