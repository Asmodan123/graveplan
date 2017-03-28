package de.hannisoft.gaveplan;

import de.hannisoft.gaveplan.export.PNGPlaceMapWriter;

public class GravePlan {
    public void run(String graveFile, String placeFile, String outputDir, String timestamp) throws Exception {
        // GraveFileReader graveReader = new GraveFileReader();
        // Map<String, Grave> graves = graveReader.read(graveFile);
        //
        // PlaceFileReader placeReader = new PlaceFileReader();
        // Map<String, PlaceMap> placeMaps = placeReader.read(placeFile, graves);
        //
        // String dueDay = timestamp.substring(6, 8) + "." + timestamp.substring(4, 6) + "." + timestamp.substring(0, 4);
        // PlaceMapWriter writer = new PlaceMapWriter();
        // writer.write(outputDir, placeMaps, dueDay);

        PNGPlaceMapWriter pngWriter = new PNGPlaceMapWriter();
        pngWriter.drawDefaultMap();

        // ZipCreator zipper = new ZipCreator();
        // zipper.zipFolder(outputDir, outputDir + "Lageplan_" + timestamp + ".zip");
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
        GravePlan gravePlane = new GravePlan();
        gravePlane.run(graveFile, placeFile, outputDir, timestamp);
    }

}
