package de.hannisoft.graveplan.export;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import de.hannisoft.graveplan.export.HTMLGraveMapWriter.OutputType;
import de.hannisoft.graveplan.model.ElementType;
import de.hannisoft.graveplan.model.GraveMap;
import de.hannisoft.graveplan.model.PlanElement;
import de.hannisoft.graveplan.properties.ElementsReader;

public class GraveMapWriter {
    public void write(String outputDir, Map<String, GraveMap> graveMaps, String dueDay, boolean allData) throws Exception {
        // PNGGraveMapWriter png = new PNGGraveMapWriter();
        if (allData) {
            writeHtmlGraveMap(outputDir, graveMaps, OutputType.RUNTIME, dueDay, true);
            writeHtmlGraveSiteFiles(outputDir, graveMaps, dueDay);
        }
        writeHtmlGraveMap(outputDir, graveMaps, OutputType.REFERENCE, dueDay, allData);
        writeHtmlSearchSiteFiles(outputDir, graveMaps, dueDay, allData);
    }

    private void writeHtmlGraveMap(String outputDir, Map<String, GraveMap> placeMaps, OutputType type, String dueDay,
            boolean allData) throws Exception {
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
            writer.write(entry.getValue(), dueDay, allData);
        }
    }

    private void writeHtmlGraveSiteFiles(String outputDir, Map<String, GraveMap> graveMaps, String dueDay) throws IOException {
        File graveSiteDir = new File(outputDir, "grabstätten");
        HTMLGraveSiteWriter writer = new HTMLGraveSiteWriter(graveSiteDir);
        for (GraveMap graveMap : graveMaps.values()) {
            writer.write(graveMap.graveSites(), dueDay);
        }
    }

    private void writeHtmlSearchSiteFiles(String outputDir, Map<String, GraveMap> graveMaps, String dueDay, boolean allData)
            throws IOException {
        File graveSiteDir = new File(outputDir, "suche");
        HTMLSearchSiteWrite writer = new HTMLSearchSiteWrite(allData, graveSiteDir);
        writer.write(graveMaps, dueDay);
    }

}
