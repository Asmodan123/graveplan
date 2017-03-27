package de.hannisoft.gaveplan.export;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import de.hannisoft.gaveplan.model.Place;
import de.hannisoft.gaveplan.model.PlaceMap;

public class HTMLPlaceMapWriter {
    public enum OutputType {
        REFERENCE, RUNTIME
    }

    private final File dir;
    private final OutputType type;

    public HTMLPlaceMapWriter(String outputDir, OutputType outputType) throws Exception {
        this.type = outputType;
        this.dir = new File(outputDir);
        this.dir.mkdirs();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        exportResource("style.css", getBackgroundStyles());
        exportResource("grave.js", null);
    }

    private InputStream getBackgroundStyles() {
        StringBuffer sb = new StringBuffer();
        if (type == OutputType.REFERENCE) {
            sb.append("\n");
            sb.append("td.belegt {background-color: #F3F781}\n");
            sb.append("td.frei {background-color: #B3FFB3}\n");
            sb.append("td.ref {background-color: #81BEF7}\n");
        } else {
            for (int i = 0; i * 7 < 255; i++) {
                // td.ref {background-color: #81BEF7}
                sb.append("\n").append("td.LZ").append(i).append(" {background-color: #");
                String col = "00" + Integer.toHexString(255 - i * 7);
                col = col.substring(col.length() - 2);
                sb.append("FFFF").append(col);
                sb.append("}");
            }
        }
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    public void write(String field, PlaceMap placeMap, String dueDay) throws IOException {
        File html = new File(dir, field + ".html");
        html.createNewFile();
        try (PrintWriter out = new PrintWriter(html)) {
            writeHeader(out, field, placeMap.getPlaceCount(), dueDay);
            // writeColGroup(out, placeMap);
            writeTableHeader(out, placeMap);
            writeTableContent(out, placeMap);
            writeTableFooter(out, placeMap);
            writeFooter(out);
        }
    }

    private void writeHeader(PrintWriter out, String fieldName, int placeCount, String dueDay) {
        out.println("<!doctype html>");
        out.println("<html>");
        out.println("  <head>");
        out.println("    <meta charset=\"utf-8\">");
        out.println("    <title>Feld " + fieldName + "</title>");
        out.println("    <link rel=\"stylesheet\" href=\"style.css\">");
        out.println("  </head>");
        out.println("  <body>");
        out.println("    <h1>Feld " + fieldName + " (" + dueDay + ")</h1>");
        out.println("");
        out.println("    <table width=\"" + String.valueOf(placeCount * 140 + 40) + "\">");
    }

    private void writeColGroup(PrintWriter out, PlaceMap placeMap) {
        out.println("      <colgroup>");
        out.println("        <col span=\"1\" style=\"width:50\" />");
        long size = 10;
        for (int i = 0; i < placeMap.getPlaceCount(); i++) {
            out.println("        <col span=\"1\" style=\"width:" + String.valueOf(size) + "%;\" />");
        }
        out.println("      </colgroup>");
    }

    private void writeTableHeader(PrintWriter out, PlaceMap placeMap) {
        out.println("      <thead>");
        out.println("        <tr>");
        out.println("          <th class=\"col\">Platz &rarr; Reihe&darr;</th>");
        for (int i = placeMap.getPlaceCount() - 1; i >= 0; i--) {
            int place = i - placeMap.getDeltaPlace();
            if (place >= 0) {
                place++;
            }
            String placeStr = String.format("%02d", place);
            out.println("          <th>" + placeStr + "</th>");
        }
        out.println("          <th class=\"col\">Platz &larr; Reihe&darr;</th>");
        out.println("        </tr>");
        out.println("      </thead>");
    }

    private void writeTableFooter(PrintWriter out, PlaceMap placeMap) {
        out.println("      <tfoot>");
        out.println("        <tr>");
        out.println("          <td class=\"reihe\">Reihe&uarr; Platz &rarr;</td>");
        for (int i = placeMap.getPlaceCount() - 1; i >= 0; i--) {
            int place = i - placeMap.getDeltaPlace();
            if (place >= 0) {
                place++;
            }
            String placeStr = String.format("%02d", place);
            out.println("          <td class=\"reihe\">" + placeStr + "</td>");
        }
        out.println("          <td class=\"reihe\">Reihe&uarr; Platz &larr;</td>");
        out.println("        </tr>");
        out.println("      </tfoot>");
    }

    private void writeTableContent(PrintWriter out, PlaceMap placeMap) {
        out.println("      <tbody>");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (type == OutputType.RUNTIME) {
            dateFormat = new SimpleDateFormat("yyyy");
        }
        for (int i = placeMap.getRowCount() - 1; i >= 0; i--) {
            out.println("        <tr>");
            int row = i - placeMap.getDeltaRow();
            if (row >= 0) {
                row++;
            }
            String rowStr = String.format("%02d", row);
            out.println("          <td class=\"reihe\">" + rowStr + "</td>");
            for (int j = placeMap.getPlaceCount() - 1; j >= 0; j--) {
                Place place = placeMap.getPlace(i, j);
                try {
                    StringBuilder sb = new StringBuilder();
                    if (place == null) {
                        sb.append("          <td/>");
                    } else {
                        sb.append("          <td class=\"");
                        sb.append(place.getClassesStirng());
                        sb.append("\"");
                        if (place.isRef()) {
                            sb.append("data-owner=\"");
                            sb.append(escapeHtml4(place.getReference()));
                            sb.append("\"");
                        }
                        sb.append(">");
                        if (place.getDeceased() != null) {
                            sb.append(escapeHtml4(place.getDeceased()));
                        }
                        if (type == OutputType.REFERENCE) {
                            if (place.getDateOfDeatch() != null) {
                                sb.append("<br/>+ ");
                                sb.append(dateFormat.format(place.getDateOfDeatch()));
                            }
                        } else {
                            if (place.getGrave() != null && place.getGrave().getValidTo() != null) {
                                sb.append("<br/>bis ");
                                sb.append(dateFormat.format(place.getGrave().getValidTo()));
                            }
                        }
                        sb.append("</td>");
                    }
                    out.println(sb.toString());
                } catch (Exception e) {
                    System.err.println(e.getClass().getSimpleName() + " while printing place " + place + ": " + e.getMessage());
                    e.printStackTrace();
                    out.println("        <td/>");
                }
            }
            out.println("          <td class=\"reihe\">" + rowStr + "</td>");
            out.println("        </tr>");
        }
        out.println("      </tbody>");
    }

    private void writeFooter(PrintWriter out) {
        out.println("    </table>");
        out.println("    <script src=\"grave.js\"></script>");
        out.println("  </body>");
        out.println("</html>");
    }

    private void exportResource(String resourceName, InputStream addContent) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(new File(dir, resourceName));
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }

            if (addContent != null) {
                while ((readBytes = addContent.read(buffer)) > 0) {
                    resStreamOut.write(buffer, 0, readBytes);
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
    }
}
