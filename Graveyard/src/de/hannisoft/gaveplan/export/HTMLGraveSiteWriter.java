package de.hannisoft.gaveplan.export;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import de.hannisoft.gaveplan.model.Grave;
import de.hannisoft.gaveplan.model.GraveSite;

public class HTMLGraveSiteWriter {
    private final File dir;

    public HTMLGraveSiteWriter(File outputDir) {
        this.dir = outputDir;
        this.dir.mkdirs();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    public void write(Collection<GraveSite> graveSites, String dueDay) throws IOException {
        for (GraveSite graveSite : graveSites) {
            File graveSiteFile = new File(dir, graveSite.getFileName());
            graveSiteFile.createNewFile();
            try (PrintWriter out = new PrintWriter(graveSiteFile)) {
                writeHeader(out, graveSite, dueDay);
                writeCriterias(out, graveSite.getCriterias());
                writeOwner(out, graveSite);
                writeTableHeader(out, graveSite);
                writeTableBody(out, graveSite);
                writeFooter(out);
            }
        }
    }

    private void writeCriterias(PrintWriter out, List<String> criterias) {
        if (!criterias.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String crit : criterias) {
                sb.append(crit).append(", ");
            }
            sb.setLength(sb.length() - 2);
            out.print("Auswahlkriterien: ");
            out.print(sb.toString());
            out.print("<br/>");
            out.print("<br/>");
        }
    }

    private void writeOwner(PrintWriter out, GraveSite graveSite) {
        out.print("    Nutzungsrecht bis ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (graveSite.getValidTo() != null) {
            out.print(dateFormat.format(graveSite.getValidTo()));
        }
        out.println("<br/>");

        out.print("    ");
        out.print(graveSite.getName());
        out.println("<br/>");

        out.println("    <br/>");

        out.println("    <b>Nutzungsberechtigter:</b></br>");

        out.print("    ");
        out.print(graveSite.getOwner().getFirstName());
        out.print(" ");
        out.print(graveSite.getOwner().getLastName());
        out.println("<br/>");

        out.print("    ");
        out.print(graveSite.getOwner().getStreet());
        out.println("<br/>");

        out.print("    ");
        out.print(graveSite.getOwner().getZipAndTown());
        out.println("<br/>");

        out.println("    <br/>");
    }

    private void writeTableHeader(PrintWriter out, GraveSite graveSite) {
        out.println("    <table width=\"" + String.valueOf(graveSite.getPlaceSize() * 140 + 40) + "\">");

        out.println("      <thead>");
        out.println("        <tr>");
        out.println("          <th class=\"col\">Platz &rarr; Reihe&darr;</th>");
        for (int i = graveSite.getPlaceSize() - 1; i >= 0; i--) {
            String plcaeStr = String.format("%02d", graveSite.getPlaceInt() + i);
            out.println("          <th>" + plcaeStr + "</th>");
        }
        out.println("        </tr>");
        out.println("      </thead>");

    }

    private void writeHeader(PrintWriter out, GraveSite graveSite, String dueDay) {
        out.println("<!doctype html>");
        out.println("<html>");
        out.println("  <head>");
        out.println("    <meta charset=\"utf-8\">");
        out.println("    <title>Feld " + graveSite.getId() + "</title>");
        out.println("    <link rel=\"stylesheet\" href=\"../belegung/style.css\">");
        out.println("    <link rel=\"stylesheet\" href=\"../plan/nav.css\">");
        out.println("  </head>");
        out.println("  <body>");
        out.println(HTMLConstants.INSIDE_ZIP);
        out.println("    <ul>");
        out.println("      <li><a href=\"../Friedhofsplan.html\">Übersicht</a></li>");
        out.println("      <li><a href=\"../suche/Suche.html\">Suche</a></li>");
        out.print("      <li><a href=\"../belegung/");
        out.print(graveSite.getField());
        out.print(".html#");
        out.print(graveSite.getId().replace('/', '_'));
        out.print("\">Belegung - Feld ");
        out.print(graveSite.getField());
        out.println("</a></li>");
        out.print("      <li><a href=\"../laufzeit/");
        out.print(graveSite.getField());
        out.print(".html#");
        out.print(graveSite.getId().replace('/', '_'));
        out.print("\">Restlaufzeit - Feld ");
        out.print(graveSite.getField());
        out.println("</a></li>");
        out.print("      <li><a class=\"active\" href=\"#\">Grabstst&auml;tte ");
        out.print(graveSite.getId());
        out.print("</a></li>");
        out.println(
                "      <li style=\"float: right\"><a href=\"mailto:johannes.ahlers@gmx.de?subject=Frage zum Friedhofsplan\">Hilfe</a></li>");
        out.println("    </ul>");
        out.println("    <h1>Gabst&auml;tte " + graveSite.getId() + " (" + dueDay + ")</h1>");
        out.println("    <h3>" + graveSite.getType().getName() + "</h3>");
        // out.println(" <table width=\"" + String.valueOf(graveCount * 140 + 40) + "\">");
    }

    private void writeTableBody(PrintWriter out, GraveSite graveSite) {
        out.println("      <tbody>");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        for (int i = graveSite.getRowSize() - 1; i >= 0; i--) {
            out.println("        <tr>");
            int row = graveSite.getRowInt() + i;
            String rowStr = String.format("%02d", row);
            out.println("          <td class=\"reihe\">" + rowStr + "</td>");
            for (int j = graveSite.getPlaceSize() - 1; j >= 0; j--) {
                int place = graveSite.getPlaceInt() + j;
                StringBuilder sb = new StringBuilder();
                for (Grave grave : graveSite.getGraves()) {
                    if (grave.getRowInt() == row && grave.getPlaceInt() == place) {
                        try {
                            if (sb.length() == 0) {
                                sb.append("          <td class=\"").append(grave.getClassesStirng()).append("\">");
                            }
                            if (grave.getDeceased() != null) {
                                sb.append(escapeHtml4(grave.getDeceased()));
                            }
                            if (grave.getDateOfBirth() != null) {
                                sb.append("<br/>* ");
                                sb.append(dateFormat.format(grave.getDateOfBirth()));
                            }
                            if (grave.getDateOfDeath() != null) {
                                sb.append("<br/>+ ");
                                sb.append(dateFormat.format(grave.getDateOfDeath()));
                            }
                        } catch (Exception e) {
                            System.err.println(
                                    e.getClass().getSimpleName() + " while printing Grave " + grave + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
                if (sb.length() == 0) {
                    sb.append("          <td/>");
                } else {
                    sb.append("</td>");
                }
                out.println(sb.toString());

            }
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
