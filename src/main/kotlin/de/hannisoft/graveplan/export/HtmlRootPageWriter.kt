package de.hannisoft.de.hannisoft.graveplan.export

import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.htmlObject
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.script
import java.io.File
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.ul

class HtmlRootPageWriter {
    fun write(outputDir: String, targetFile: String, dueDay: String) {
        val html = createHTML().html {
            head {
                link(rel = "icon", href = "plan/plan.ico", type = "image/x-icon")
                link(rel = "stylesheet", href = "plan/js/bootstrap.min.css", type = "text/css")
                link(rel = "stylesheet", href = "plan/nav.css", type = "text/css")
                title("Lageplan Friedhof Pattensen")
            }
            body {
                script(src = "plan/js/bootstrap.bundle.min.js") {}
                script(src = "plan/js/vis-network.min.js") {}
                h1(classes = "insideZip") {
                    +"""
                        Wenn dieser Text sichtbar ist, wurde der Friedhofplan unvollständig ausgeführt. Wahrscheinlich wurde diese Datei direkt aus dem  
                        Zip-Archiv geöffnet. Das funktioniert nicht! Das Archiv muss komplett in ein lokales Verzeichnis entpackt werden. 
                        Dann kann die Einstiegs-Datei <u>Friedhofsplan.html</u> geöffnet werden.                        
                    """.trimIndent()
                }
                ul {
                    li { a(classes = "active", href = "#") { +"Übersicht" } }
                    li { a(href = "plan/suche/Suche.html") { +"Suche" } }
                    li { style = "float: right"
                        a(href = "mailto:johannes.ahlers@gmx.de?subject=Frage zum Friedhofsplan") { +"Hilfe" }
                    }
                    li { style = "float: right"
                        a { +"Stand: $dueDay" }
                    }
                }
                htmlObject {
                    data = "plan/Friehofsplan.svg"
                    type = "image/svg+xml"
                }
            }
        }
        File(outputDir, targetFile).writeText(html)
    }
}

