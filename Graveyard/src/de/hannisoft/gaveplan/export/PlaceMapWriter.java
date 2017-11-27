package de.hannisoft.gaveplan.export;

import java.util.Map;
import java.util.Map.Entry;

import de.hannisoft.gaveplan.export.HTMLPlaceMapWriter.OutputType;
import de.hannisoft.gaveplan.model.ElementType;
import de.hannisoft.gaveplan.model.PlaceMap;
import de.hannisoft.gaveplan.model.PlanElement;
import de.hannisoft.gaveplan.properties.ElementsReader;

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
        ElementsReader elementReader = new ElementsReader();
        Map<String, PlanElement> fieldElements = elementReader.readElements(ElementType.FELD);
        HTMLPlaceMapWriter writer = new HTMLPlaceMapWriter(outputDir, type);
        for (Entry<String, PlaceMap> entry : placeMaps.entrySet()) {
            entry.getValue().finishEdit(fieldElements);
            writer.write(entry.getKey(), entry.getValue(), dueDay);
        }

    }
}
