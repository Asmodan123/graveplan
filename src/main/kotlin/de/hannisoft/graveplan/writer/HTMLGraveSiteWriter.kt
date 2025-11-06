package de.hannisoft.graveplan.writer

import de.hannisoft.graveplan.model.GraveSite
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.io.File
import java.text.SimpleDateFormat

class HTMLGraveSiteWriter(outputDirString: String) {
    val outputDir = File(outputDirString, "grabstätten")
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")

    init {
        initOutputDir(outputDir)
    }

    fun write(graveSite: Collection<GraveSite>, dueDay: String) {
        graveSite.forEach { graveSite ->
            val html = createHTML().html {
                writeHeader(graveSite)
                body {
                    writeNavBar(graveSite, dueDay)
                    h1 { +"Grabstätte ${graveSite.id}" }
                    h3 { +"${graveSite.typeAsString}" }
                    writeCriteria(graveSite.criterias)
                    writeOwner(graveSite)
                    table {
                        style = "width: ${graveSite.placeSize * 140 + 80}px;"
                        writeTableHeader(graveSite)
                        writeTableBody(graveSite)
                    }
                }
            }
            File(outputDir, graveSite.fileName).writeText(html)
        }
    }

    private fun HTML.writeHeader(graveSite: GraveSite) {
        head {
            title("Feld $graveSite.id")
            link { rel = "stylesheet"; href = "../belegung/style.css"; type = "text/css" }
            link { rel = "stylesheet"; href = "../plan/nav.css"; type = "text/css" }

        }
    }

    private fun BODY.writeNavBar(graveSite: GraveSite, dueDay: String) {
        ul {
            li { a(href = "../Friedhofsplan.html") { +"Übersicht" } }
            li { a(href = "../suche/Suche.html") { +"Suche" } }
            li { a(href = "../belegung/${graveSite.field}.html#${graveSite.id.replace('/', '_')}") { +"Belegung - Feld ${graveSite.field}" } }
            li { a(href = "../laufzeit/${graveSite.field}.html#${graveSite.id.replace('/', '_')}") { +"Restlaufzeit - Feld ${graveSite.field}" } }
            li { a(href = "#", classes = "active") { +"Grabstätte ${graveSite.id}" } }
            li {
                style = "float: right"
                a(href = "mailto:johannes.ahlers@gmx.de?subject=Frage zum Friedhofsplan") { +"Hilfe" }
            }
            li {
                style = "float: right"
                a { +"Stand: $dueDay" }
            }
        }
    }

    private fun BODY.writeCriteria(criterias: List<String>) {
        if (criterias.isNotEmpty()) {
            +"Auswahlkriterien: ${criterias}"
        }
        br
    }

    private fun BODY.writeOwner(graveSite: GraveSite) {
        +"Nutzungsrecht bis ${if (graveSite.validTo == null) "---" else dateFormat.format(graveSite.validTo)  }"
        br
        +"${graveSite.name}"
        br

        br
        b { +"Nutzungsberechtigter:" }
        br

        +"${graveSite.owner?.firstName} ${graveSite.owner?.lastName}"
        br

        +"${graveSite.owner?.street}"
        br

        +"${graveSite.owner?.zipAndTown}"
        br

        br
    }

    private fun TABLE.writeTableHeader(graveSite: GraveSite) {
        thead {
            tr {
                th(classes = "col") {
                    +"Platz →"
                    br
                    +"Reihe ↓"
                }
                for (i in graveSite.placeSize - 1 downTo 0 ) {
                    th { +"${graveSite.placeInt + i}" }
                }
            }
        }
    }

    private fun TABLE.writeTableBody(graveSite: GraveSite) {
        tbody {
            for (i in graveSite.rowSize - 1 downTo 0) {
                val row = graveSite.rowInt + i
                tr {
                    td(classes = "reihe") { +graveSite.row }
                    for (j in graveSite.placeSize - 1 downTo 0) {
                        val place = graveSite.placeInt + j
                        graveSite.graves
                            .find { grave -> grave.place == place && grave.row == row && grave.getClassesString().isNotEmpty()}
                            ?.let { grave ->
                            td(classes = grave.getClassesString()) {
                                grave.deceased.let { +it }
                                grave.dateOfBirth?.let {
                                    br
                                    +"* ${dateFormat.format(it)}"
                                }
                                grave.dateOfDeath?.let {
                                    br
                                    +"+ ${dateFormat.format(it)}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}