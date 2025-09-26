package de.hannisoft.graveplan;

import java.io.File;
import java.util.Map;

import de.hannisoft.graveplan.excelimport.GraveFileReader;
import de.hannisoft.graveplan.excelimport.GraveSiteCriteriaFileReader;
import de.hannisoft.graveplan.excelimport.GraveSiteFileReader;
import de.hannisoft.graveplan.export.FileExporter;
import de.hannisoft.graveplan.export.GraveMapWriter;
import de.hannisoft.graveplan.export.HTMLRootPageWriter;
import de.hannisoft.graveplan.export.ZipCreator;
import de.hannisoft.graveplan.model.GraveMap;
import de.hannisoft.graveplan.model.GraveSite;

public class GravePlan {
    public static final String FILE_TOKEN_GRAVE_SITE = "Grabstätten_";
    public static final String FILE_TOKEN_GRAVE = "Verstorbene_";
    public static final String FILE_TOKEN_CRITERIA = "Auswahlkriterien_";
    public static final String FILE_ENDING = ".xls";
    public static String timestamp = "20250816";
    public static final String FILE_NAME = "Friedhofsplan_" + timestamp;
    public static final String FILE_NAME2 = "Friedhofsplan_öffentlich_" + timestamp;
    public static String importDir = "/home/johannes/Dokumente/Friedhof/export/";
    public static String outputDir = "/home/johannes/tmp/plan/" + FILE_NAME + "/";
    public static String outputDirPublic = "/home/johannes/tmp/plan/" + FILE_NAME2 + "/";

    public void run(String importDir, String outputDir, String outputDir2, String timestamp) throws Exception {
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
        writer.write(outputDir, graveMaps, dueDay, true);

        HTMLRootPageWriter rootPageWriter = new HTMLRootPageWriter();
        rootPageWriter.write(outputDir, "Friedhofsplan.html", dueDay);
        exportAdditionalFiles(outputDir);
        //
        // PNGGraveMapWriter pngWriter = new PNGGraveMapWriter();
        // pngWriter.drawDefaultMap();
        //
        ZipCreator zipper = new ZipCreator();
        zipper.zipFolder(outputDir, outputDir + "../" + FILE_NAME + ".zip");
        writer.write(outputDir2, graveMaps, dueDay, false);
        rootPageWriter.write(outputDir2, "Friedhofsplan.html", dueDay);
        exportAdditionalFiles(outputDir2);
        zipper.zipFolder(outputDir2, outputDir2 + "../" + FILE_NAME2 + ".zip");
    }

    private void exportAdditionalFiles(String outputDir) throws Exception {
        FileExporter exporter = new FileExporter();
        File destPath = new File(outputDir);
        // exporter.exportResource(destPath, "Friedhofsplan.html");

        destPath = new File(outputDir, "plan");
        destPath.mkdirs();
        exporter.exportResource(destPath, "satellit.png");
        exporter.exportResource(destPath, "Friehofsplan.svg");
        exporter.exportResource(destPath, "nav.css");

        destPath = new File(outputDir, "suche");
        destPath.mkdirs();
        exporter.exportResource(destPath, "search.css");
        exporter.exportResource(destPath, "search.js");
        exporter.exportResource(destPath, "searchicon.png");

    }

    public static void main(String[] args) throws Exception {
        if (args != null && args.length == 3) {
            importDir = args[0];
            outputDir = args[1];
            timestamp = args[2];
        }
        GravePlan gravePlane = new GravePlan();
        gravePlane.run(importDir, outputDir, outputDirPublic, timestamp);
    }

}
