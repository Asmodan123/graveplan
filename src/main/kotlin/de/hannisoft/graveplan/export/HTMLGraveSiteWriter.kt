package de.hannisoft.de.hannisoft.graveplan.export

import de.hannisoft.graveplan.model.GraveSite
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.io.File

class HTMLGraveSiteWriter(val outputDirString: String) {
    val outputDir = File(outputDirString)
    init {
        outputDir.mkdirs()
        outputDir.listFiles()?.forEach { it.delete() }
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
                        style = "width: ${graveSite.placeSize * 140 + 40}px;"
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
            li { a(href = "../belegunng/${graveSite.field}.html#${graveSite.id.replace('/', '_')}") { +"Belegung - Feld ${graveSite.field}" } }
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
    }

    private fun BODY.writeOwner(graveSite: GraveSite) {
        +"Nutzungsrecht bis ${graveSite.validTo}"
        br
        +"${graveSite.name}"
        br

        b { +"Nutzungsberechtigter:" }
        br

        +"${graveSite.owner?.firstName} ${graveSite.owner?.lastName}"
        br

        +"${graveSite.owner?.street}"
        br

        +"${graveSite.owner?.zipAndTown}"
        br
    }

    private fun TABLE.writeTableHeader(graveSite: GraveSite) {
        thead {
            tr {
                th(classes = "col") { +"Platz &rarr; Reihe &darr;" }
                for (i in graveSite.placeSize - 1 downTo 0 ) {
                    th { +"$graveSite.placeInt + i}" }
                }
            }
        }
    }

    private fun TABLE.writeTableBody(graveSite: GraveSite) {
        tbody {
            for (i in graveSite.rowSize - 1 downTo 0) {
                val row = graveSite.rowInt + i
                tr {
                    td(classes = "row") { +graveSite.row }
                    for (j in graveSite.placeSize - 1 downTo 0) {
                        val place = graveSite.placeInt + j
                        graveSite.graves.find { it.place == place && it.row == row }?.let { grave ->
                            td(classes = grave.getClassesString()) {
                                grave.deceased?.let { +it }
                                grave.dateOfBirth?.let {
                                    br
                                    +"* ${it})"
                                }
                                grave.dateOfDeath?.let {
                                    br
                                    +"+ ${it})"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}