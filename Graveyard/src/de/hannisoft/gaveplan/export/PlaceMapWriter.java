package de.hannisoft.gaveplan.export;

import java.util.Map;
import java.util.Map.Entry;

import de.hannisoft.gaveplan.export.HTMLPlaceMapWriter.OutputType;
import de.hannisoft.gaveplan.model.PlaceMap;

public class PlaceMapWriter {
    public void write(String outputDir, Map<String, PlaceMap> placeMaps) throws Exception {
        // PNGPlaceMapWriter png = new PNGPlaceMapWriter();

        writeHtmlPlaceMap(outputDir, placeMaps, OutputType.RUNTIME);
        writeHtmlPlaceMap(outputDir, placeMaps, OutputType.REFERENCE);
    }

    private void writeHtmlPlaceMap(String outputDir, Map<String, PlaceMap> placeMaps, OutputType type) throws Exception {
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
            writer.write(entry.getKey(), entry.getValue());
        }

    }
}
