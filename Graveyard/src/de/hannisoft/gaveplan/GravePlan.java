package de.hannisoft.gaveplan;

import java.util.Map;

import de.hannisoft.gaveplan.excelimport.GraveFileReader;
import de.hannisoft.gaveplan.excelimport.PlaceFileReader;
import de.hannisoft.gaveplan.export.PlaceMapWriter;
import de.hannisoft.gaveplan.model.Grave;
import de.hannisoft.gaveplan.model.PlaceMap;

public class GravePlan {
    public void run(String graveFile, String placeFile, String outputDir) throws Exception {
        GraveFileReader graveReader = new GraveFileReader();
        Map<String, Grave> graves = graveReader.read(graveFile);

        PlaceFileReader placeReader = new PlaceFileReader();
        Map<String, PlaceMap> placeMaps = placeReader.read(placeFile, graves);

        PlaceMapWriter writer = new PlaceMapWriter();
        writer.write(outputDir, placeMaps);
    }

    public static void main(String[] args) throws Exception {
        GravePlan gravePlane = new GravePlan();
        gravePlane.run(args[0], args[1], args[2]);
    }

}
