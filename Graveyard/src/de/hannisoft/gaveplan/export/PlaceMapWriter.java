package de.hannisoft.gaveplan.export;

import java.util.Map;
import java.util.Map.Entry;

import de.hannisoft.gaveplan.export.HTMLPlaceMapWriter.OutputType;
import de.hannisoft.gaveplan.model.PlaceMap;

public class PlaceMapWriter {
    public void write(String outputDir, Map<String, PlaceMap> placeMaps, String dueDay) throws Exception {
        // PNGPlaceMapWriter png = new PNGPlaceMapWriter();

        writeHtmlPlaceMap(outputDir, placeMaps, OutputType.RUNTIME, dueDay);
        writeHtmlPlaceMap(outputDir, placeMaps, OutputType.REFERENCE, dueDay);
    }

    private void writeHtmlPlaceMap(String outputDir, Map<String, PlaceMap> placeMaps, OutputType type, String dueDay)
            throws Exception {
        switch (type) {
            case REFERENCE:
                outputDir = outputDir + "belegung/";
                break;
            case RUNTIME:
                outputDir = outputDir + "laufzeit/";
                break;
        }
        HTMLPlaceMapWriter writer = new HTMLPlaceMapWriter(outputDir, type);
        for (Entry<String, PlaceMap> entry : placeMaps.entrySet()) {
            entry.getValue().finishEdit();
            writer.write(entry.getKey(), entry.getValue(), dueDay);
        }

    }
}
