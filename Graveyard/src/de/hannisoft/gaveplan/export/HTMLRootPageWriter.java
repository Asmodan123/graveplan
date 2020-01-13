package de.hannisoft.gaveplan.export;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class HTMLRootPageWriter {
    public void write(String outputDir, String targetFile, String dueDay) throws IOException {
        File html = new File(outputDir, targetFile);
        html.createNewFile();
        try (PrintWriter out = new PrintWriter(html)) {
            writePage(out, dueDay);
        }
    }

    private void writePage(PrintWriter out, String dueDay) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"de\">");
        out.println("<head>");
        out.println("<meta charset=\"utf-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0;\" />");
        out.println("<style>");
        out.println("object {");
        out.println("    width: 100%;");
        out.println("    height: 100%");
        out.println("}");
        out.println("</style>");
        out.println("<link rel=\"stylesheet\" href=\"plan/nav.css\">");
        out.println("<title>Lageplan Friedhof Pattensen</title>");
        out.println("</head>");
        out.println("<body>");
        out.println(
                "    <h1 class=\"insideZip\">Wenn dieser Text sichtbar ist, wurde der Friedhofplan unvollständig ausgeführt. Wahrscheinlich wurde diese Datei direkt aus dem Zip-Archiv geöffnet. Das funktioniert nicht! Das Archiv muss komplett in ein lokales Verzeichnis entpackt werden. Dann kann die Einstiegs-Datei <u>Friedhofsplan.html</u> geöffnet werden.</h1>");
        out.println("    <ul>");
        out.println("        <li><a class=\"active\" href=\"#\">Übersicht</a></li>");
        out.println("        <li><a href=\"suche/Suche.html\">Suche</a></li>");
        out.println(
                "        <li style=\"float: right\"><a href=\"mailto:johannes.ahlers@gmx.de?subject=Frage zum Friedhofsplan\">Hilfe</a></li>");
        out.println("        <li style=\"float: right\"><a>Stand: " + dueDay + "</a></li>");
        out.println("    </ul>");
        out.println("    <object data=\"plan/Friehofsplan.svg\" type=\"image/svg+xml\" />");
        out.println("</body>");
        out.println("</html>");
    }
}
