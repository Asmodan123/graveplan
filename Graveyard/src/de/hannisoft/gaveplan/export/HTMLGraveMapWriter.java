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

import de.hannisoft.gaveplan.model.Grave;
import de.hannisoft.gaveplan.model.GraveMap;

public class HTMLGraveMapWriter {
    public enum OutputType {
        REFERENCE, RUNTIME
    }

    private final File dir;
    private final OutputType type;

    public HTMLGraveMapWriter(String outputDir, OutputType outputType) throws Exception {
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
        // exportResource("grave.js", null);
    }

    private InputStream getBackgroundStyles() {
        StringBuffer sb = new StringBuffer();
        if (type == OutputType.REFERENCE) {
            sb.append("\n");
            sb.append("td.busy {background-color: #F3F781}\n");
            sb.append("td.free {background-color: #B3FFB3}\n");
            sb.append("td.ref {background-color: #81BEF7}\n");
            sb.append("td.broached {background-color: #FFA500}\n");
            sb.append("td.stele {background-color: #FF0000}\n");
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

    public void write(String field, GraveMap graveMap, String dueDay) throws IOException {
        File html = new File(dir, field + ".html");
        html.createNewFile();
        try (PrintWriter out = new PrintWriter(html)) {
            writeHeader(out, field, graveMap.getPlaceCount(), dueDay);
            // writeColGroup(out, placeMap);
            writeTableHeader(out, graveMap);
            writeTableContent(out, graveMap);
            writeTableFooter(out, graveMap);
            writeFooter(out);
        }
    }

    private void writeHeader(PrintWriter out, String fieldName, int graveCount, String dueDay) {
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
        out.println("    <table width=\"" + String.valueOf(graveCount * 140 + 40) + "\">");
    }

    private void writeTableHeader(PrintWriter out, GraveMap gaveMap) {
        out.println("      <thead>");
        out.println("        <tr>");
        out.println("          <th class=\"col\">Platz &rarr; Reihe&darr;</th>");
        for (int i = gaveMap.getPlaceCount() - 1; i >= 0; i--) {
            int place = i - gaveMap.getDeltaPlace();
            if (place >= 0) {
                place++;
            }
            String graveStr = String.format("%02d", place);
            out.println("          <th>" + graveStr + "</th>");
        }
        out.println("          <th class=\"col\">Platz &larr; Reihe&darr;</th>");
        out.println("        </tr>");
        out.println("      </thead>");
    }

    private void writeTableFooter(PrintWriter out, GraveMap graveMap) {
        out.println("      <tfoot>");
        out.println("        <tr>");
        out.println("          <td class=\"reihe\">Reihe&uarr; Platz &rarr;</td>");
        for (int i = graveMap.getPlaceCount() - 1; i >= 0; i--) {
            int place = i - graveMap.getDeltaPlace();
            if (place >= 0) {
                place++;
            }
            String graveStr = String.format("%02d", place);
            out.println("          <td class=\"reihe\">" + graveStr + "</td>");
        }
        out.println("          <td class=\"reihe\">Reihe&uarr; Platz &larr;</td>");
        out.println("        </tr>");
        out.println("      </tfoot>");
    }

    private void writeTableContent(PrintWriter out, GraveMap graveMap) {
        out.println("      <tbody>");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (type == OutputType.RUNTIME) {
            dateFormat = new SimpleDateFormat("yyyy");
        }
        for (int i = graveMap.getRowCount() - 1; i >= 0; i--) {
            out.println("        <tr>");
            int row = i - graveMap.getDeltaRow();
            if (row >= 0) {
                row++;
            }
            String rowStr = String.format("%02d", row);
            out.println("          <td class=\"reihe\">" + rowStr + "</td>");
            for (int j = graveMap.getPlaceCount() - 1; j >= 0; j--) {
                Grave grave = graveMap.getGrave(i, j);
                try {
                    StringBuilder sb = new StringBuilder();
                    if (grave == null) {
                        sb.append("          <td/>");
                    } else {
                        sb.append("          <td class=\"");
                        sb.append(grave.getClassesStirng());
                        sb.append("\"");
                        sb.append(" style=\"cursor:pointer\" ");
                        sb.append(" onclick=\"location.href='../grabst&auml;tten/").append(grave.getGraveSite().getFileName())
                                .append("'\" ");
                        sb.append(">");
                        // if (grave.isRef()) {
                        // sb.append("data-owner=\"");
                        // sb.append(escapeHtml4(grave.getReference()));
                        // sb.append("\"");
                        // }
                        // sb.append(">");

                        sb.append("<a href=\"../grabst&auml;tten/").append(grave.getGraveSite().getFileName())
                                .append("\" style=\"text-decoration:none;color:black\"><div style=\"display: block;\">");
                        if (grave.getDeceased() != null) {
                            sb.append(escapeHtml4(grave.getDeceased()));
                        }
                        if (type == OutputType.REFERENCE) {
                            if (grave.getDateOfDeatch() != null) {
                                sb.append("<br/>+ ");
                                sb.append(dateFormat.format(grave.getDateOfDeatch()));
                            }
                        } else {
                            if (grave.getGraveSite() != null && grave.getGraveSite().getValidTo() != null) {
                                sb.append("<br/>bis ");
                                sb.append(dateFormat.format(grave.getGraveSite().getValidTo()));
                            }
                        }
                        sb.append("</div></a>");
                        sb.append("</td>");
                    }
                    out.println(sb.toString());
                } catch (Exception e) {
                    System.err.println(e.getClass().getSimpleName() + " while printing Grave " + grave + ": " + e.getMessage());
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
        // out.println(" <script src=\"grave.js\"></script>");
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