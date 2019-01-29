package de.hannisoft.gaveplan.export;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Map;

import de.hannisoft.gaveplan.model.Grave;
import de.hannisoft.gaveplan.model.GraveMap;
import de.hannisoft.gaveplan.model.GraveSite;
import de.hannisoft.gaveplan.model.Owner;

public class HTMLSearchSiteWrite {
    private final File dir;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public HTMLSearchSiteWrite(File graveSiteDir) {
        this.dir = graveSiteDir;
        this.dir.mkdirs();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    public void write(Map<String, GraveMap> graveMaps, String dueDay) throws IOException {
        File html = new File(dir, "Suche.html");
        html.createNewFile();
        try (PrintWriter out = new PrintWriter(html)) {
            writeHeader(out, dueDay);
            writeTableBody(out, graveMaps);
            writeFooter(out);
        }

    }

    private void writeHeader(PrintWriter out, String dueDay) {
        out.println("<!doctype html>");
        out.println("<html>");
        out.println("  <head>");
        out.println("    <meta charset=\"utf-8\" name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        out.println("    <title>Suche</title>");
        out.println("    <link rel=\"stylesheet\" href=\"search.css\">");
        out.println("    <script src=\"search.js\"></script>");
        out.println("  </head>");
        out.println("  <body>");
        out.println("    <h1>Suche auf dem Friedhof (Stand: " + dueDay + ")</h1>");
        out.println(
                "    <input type=\"text\" id=\"searchInput\" onkeyup=\"onSearchFieldKeyup()\" placeholder=\"Suchtext eingeben...\" title=\"Suchtext\">");
        out.println("    <table width=\"100%\">");
        out.println("      <thead>");
        out.println("        <tr>");
        out.println("          <th>Feld</th>");
        out.println("          <th>Grabstätte</th>");
        out.println("          <th>Grabart</th>");
        out.println("          <th>Größe</th>");
        out.println("          <th>Laufzeit bis</th>");
        out.println("          <th>Nutzungsrecht</th>");
        out.println("          <th>Verstorbene</th>");
        out.println("        </tr>");
        out.println("      </thead>");
    }

    private void writeTableBody(PrintWriter out, Map<String, GraveMap> graveMaps) {
        out.println("      <tbody id=\"searchResult\">");
        for (GraveMap graveMap : graveMaps.values()) {
            for (GraveSite graveSite : graveMap.graveSites()) {
                out.print("        <tr data-search=\"");
                writeSearchData(out, graveSite);
                out.println("\" style=\"display:none\">");
                writeTD_Feld(out, graveSite);
                writeTD_Grabstaette(out, graveSite);
                writeTD_Grabart(out, graveSite);
                writeTD_Groesse(out, graveSite);
                writeTD_Laufzeit(out, graveSite);
                writeTD_Nutzer(out, graveSite);
                writeTD_Verstorbene(out, graveSite);
                out.println("        </tr>");
            }
        }
        out.println("      </tbody>");
    }

    private void writeSearchData(PrintWriter out, GraveSite graveSite) {
        out.print(graveSite.getField());
        out.print(" ");
        out.print(graveSite.getField());
        out.print(" ");
        out.print(graveSite.getName());
        out.print(" ");
        out.print(graveSite.getPlace());
        out.print(" ");
        out.print(graveSite.getRow());
        for (String criteria : graveSite.getCriterias()) {
            out.print(" ");
            out.print(criteria);
        }
        out.print(" ");
        out.print(graveSite.getType());
        out.print(" ");
        out.print(graveSite.getType().name());
        if (graveSite.getValidFrom() != null) {
            out.print(" ");
            out.print(dateFormat.format(graveSite.getValidFrom()));
        }
        if (graveSite.getValidTo() != null) {
            out.print(" ");
            out.print(dateFormat.format(graveSite.getValidTo()));
        }
        Owner owner = graveSite.getOwner();
        out.print(" ");
        out.print(owner.getFirstName());
        out.print(" ");
        out.print(owner.getLastName());
        out.print(" ");
        out.print(owner.getStreet());
        out.print(" ");
        out.print(owner.getZipAndTown());
        for (Grave grave : graveSite.getGraves()) {
            if (!grave.isEmpty()) {
                out.print(" ");
                out.print(grave.getDeceased());
                if (grave.getDateOfBirth() != null) {
                    out.print(" ");
                    out.print(dateFormat.format(grave.getDateOfBirth()));
                }
                if (grave.getDateOfDeath() != null) {
                    out.print(" ");
                    out.print(dateFormat.format(grave.getDateOfDeath()));
                }
            }
        }
    }

    private void writeTD_Feld(PrintWriter out, GraveSite graveSite) {
        out.print("          <td><a href=\"../belegung/");
        out.print(graveSite.getField());
        out.print(".html\">");
        out.print(graveSite.getField());
        out.println("</a></td>");
    }

    private void writeTD_Grabstaette(PrintWriter out, GraveSite graveSite) {
        out.print("          <td><a href=\"../grabstätten/");
        out.print(graveSite.getFileName());
        out.print("\">");
        out.print(graveSite.getId());
        out.println("</a></td>");
    }

    private void writeTD_Grabart(PrintWriter out, GraveSite graveSite) {
        out.print("          <td class=\"left\">");
        out.print(graveSite.getType().getName());
        out.println("</td>");
    }

    private void writeTD_Groesse(PrintWriter out, GraveSite graveSite) {
        out.print("          <td>");
        out.print(graveSite.getSize());
        out.println("</td>");
    }

    private void writeTD_Laufzeit(PrintWriter out, GraveSite graveSite) {
        out.print("          <td>");
        if (graveSite.getValidTo() != null) {
            out.print(dateFormat.format(graveSite.getValidTo()));
        }
        out.println("</td>");
    }

    private void writeTD_Nutzer(PrintWriter out, GraveSite graveSite) {
        out.print("          <td class=\"left\">");
        out.print(graveSite.getOwner().getFirstName());
        out.print(" ");
        out.print(graveSite.getOwner().getLastName());
        out.println("</td>");
    }

    private void writeTD_Verstorbene(PrintWriter out, GraveSite graveSite) {
        out.print("          <td class=\"left\">");
        for (Grave grave : graveSite.getGraves()) {
            if (!grave.isEmpty()) {
                out.print(grave.getDeceased());
                out.print(", ");
            }
        }
        out.println("</td>");
    }

    private void writeFooter(PrintWriter out) {
        out.println("    </table>");
        out.println("  </body>");
        out.println("</html>");
    }

}
