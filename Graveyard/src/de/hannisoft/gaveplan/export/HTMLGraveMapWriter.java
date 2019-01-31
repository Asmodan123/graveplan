package de.hannisoft.gaveplan.export;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import de.hannisoft.gaveplan.model.Grave;
import de.hannisoft.gaveplan.model.GraveMap;
import de.hannisoft.gaveplan.model.PlanElement;

public class HTMLGraveMapWriter {
    public enum OutputType {
        REFERENCE, RUNTIME
    }

    private final File dir;
    private final OutputType type;
    private FileExporter exporter = new FileExporter();

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
        exporter.exportResource(dir, "style.css", getBackgroundStyles());
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

    public void write(GraveMap graveMap, String dueDay) throws IOException {
        File html = new File(dir, graveMap.getFieldName() + ".html");
        html.createNewFile();
        try (PrintWriter out = new PrintWriter(html)) {
            writeHeader(out, graveMap.getField(), graveMap.getPlaceCount(), dueDay);
            // writeColGroup(out, placeMap);
            writeTableHeader(out, graveMap);
            writeTableContent(out, graveMap);
            writeTableFooter(out, graveMap);
            writeFooter(out);
        }
    }

    private void writeHeader(PrintWriter out, PlanElement field, int graveCount, String dueDay) {
        out.println("<!doctype html>");
        out.println("<html>");
        out.println("  <head>");
        out.println("    <meta charset=\"utf-8\">");
        out.println("    <title>Feld " + field.getLabel() + "</title>");
        out.println("    <link rel=\"stylesheet\" href=\"style.css\">");
        out.println("    <link rel=\"stylesheet\" href=\"../plan/nav.css\">");
        out.println("  </head>");
        out.println("  <body>");
        out.println("    <ul>");
        out.println("      <li><a href=\"../Friedhofsplan.html\">Übersicht</a></li>");
        out.println("      <li><a href=\"../suche/Suche.html\">Suche</a></li>");
        if (type == OutputType.REFERENCE) {
            out.print("      <li><a class=\"active\" href=\"#\">Belegung - Feld ");
            out.print(field.getName());
            out.println("</a></li>");
            out.print("      <li><a href=\"../laufzeit/");
            out.print(field.getName());
            out.print(".html\">Restlaufzeit - Feld ");
            out.print(field.getName());
            out.println("</a></li>");
        } else {
            out.print("      <li><a href=\"../belegung/");
            out.print(field.getName());
            out.print(".html\">Belegung - Feld ");
            out.print(field.getName());
            out.println("</a></li>");
            out.print("      <li><a class=\"active\" href=\"#\">Restlaufzeit - Feld ");
            out.print(field.getName());
            out.print("</a></li>");
        }
        out.println(
                "      <li style=\"float: right\"><a href=\"mailto:johannes.ahlers@gmx.de?subject=Frage zum Friedhofsplan\">Hilfe</a></li>");
        out.println("    </ul>");
        out.println("    <h1>Feld " + field.getLabel() + " (" + dueDay + ")</h1>");
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
                        if (grave.isRef()) {
                            sb.append("\" id=\"");
                            sb.append(grave.getGraveSite().getId().replace('/', '_'));
                        }
                        sb.append("\" style=\"cursor:pointer\" ");
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
                            if (grave.getDateOfBirth() != null) {
                                sb.append("<br/>* ");
                                sb.append(dateFormat.format(grave.getDateOfBirth()));
                            }
                            if (grave.getDateOfDeath() != null) {
                                sb.append("<br/>+ ");
                                sb.append(dateFormat.format(grave.getDateOfDeath()));
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
        out.println("  </body>");
        out.println("</html>");
    }

}
