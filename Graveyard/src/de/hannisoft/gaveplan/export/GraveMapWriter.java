package de.hannisoft.gaveplan.export;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import de.hannisoft.gaveplan.export.HTMLGraveMapWriter.OutputType;
import de.hannisoft.gaveplan.model.ElementType;
import de.hannisoft.gaveplan.model.GraveMap;
import de.hannisoft.gaveplan.model.PlanElement;
import de.hannisoft.gaveplan.properties.ElementsReader;

public class GraveMapWriter {
    public void write(String outputDir, Map<String, GraveMap> graveMaps, String dueDay) throws Exception {
        // PNGGraveMapWriter png = new PNGGraveMapWriter();
        writeHtmlGraveMap(outputDir, graveMaps, OutputType.RUNTIME, dueDay);
        writeHtmlGraveMap(outputDir, graveMaps, OutputType.REFERENCE, dueDay);
        writeHtmlGraveSiteFiles(outputDir, graveMaps, dueDay);
    }

    private void writeHtmlGraveMap(String outputDir, Map<String, GraveMap> placeMaps, OutputType type, String dueDay)
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
        HTMLGraveMapWriter writer = new HTMLGraveMapWriter(outputDir, type);
        for (Entry<String, GraveMap> entry : placeMaps.entrySet()) {
            entry.getValue().finishEdit(fieldElements);
            writer.write(entry.getKey(), entry.getValue(), dueDay);
        }
    }

    private void writeHtmlGraveSiteFiles(String outputDir, Map<String, GraveMap> graveMaps, String dueDay) throws IOException {
        File graveSiteDir = new File(outputDir, "grabstätten");
        HTMLGraveSiteWriter writer = new HTMLGraveSiteWriter(graveSiteDir);
        for (GraveMap graveMap : graveMaps.values()) {
            writer.write(graveMap.graveSites(), dueDay);
        }
    }

}
