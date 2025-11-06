package de.hannisoft.de.hannisoft.graveplan.writer

import de.hannisoft.graveplan.model.GraveMap
import de.hannisoft.graveplan.model.GraveSite
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.io.File
import java.text.SimpleDateFormat

class HTMLSearchSiteWrite(outputDirString: String) {
    val outputDir = File(outputDirString, "suche")
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy")

    fun write(graveMaps: Map<String, GraveMap>, dueDay: String, allData: Boolean) {
        val html = createHTML().html {
            writeHeader()
            body {
                writeNavBar(dueDay)
                h1 { +"Suche auf dem Friedhof" }
                input(type = InputType.text) {
                    id = "searchInput"
                    autoFocus = true
                    placeholder = "Suchbegriff eingeben..."
                    title = "Suchbegriff"
                    onKeyUp = "onSearchFieldKeyup()"
                }
                table {
                    style = "width: 100%;"
                    writeTableHeader(allData)
                    writeTableBody(graveMaps, allData)
                }
            }
        }
        File(outputDir, "Suche.html").writeText(html)
    }

    private fun HTML.writeHeader() {
        head {
            title("Suche")
            link { rel = "stylesheet"; href = "search.css"; type = "text/css" }
            link { rel = "stylesheet"; href = "../plan/nav.css"; type = "text/css" }
            script(src = "search.js") {}
        }
    }

    private fun BODY.writeNavBar(dueDay: String) {
        ul {
            li { a(href = "../Friedhofsplan.html") { +"Übersicht" } }
            li { a(href = "#", classes = "active") { +"Suche" } }
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

    private fun TABLE.writeTableHeader(allData: Boolean) {
        thead {
            tr {
                th { +"Feld" }
                th { +"Grabstätte" }
                th { +"Grabart" }
                if (allData) {
                    th { +"Laufzeit bis" }
                    th { +"Nutzungsrecht" }
                    th { +"Merkmale" }
                }
                th { +"Verstorbene" }
            }
        }
    }
    private fun TABLE.writeTableBody(graveMap: Map<String, GraveMap>, allData: Boolean) {
        tbody {
            id = "searchResult"
            graveMap.values.forEach { graveMap ->
                graveMap.graveSites().forEach { graveSite ->
                    tr {
                        style = "display:none;"
                        attributes["data-search"] = createSearchData(graveSite, allData)
                        td { a(href = "../belegung/${graveSite.field}.html#${graveSite.id.replace('/', '_')}") { +graveSite.field } }
                        td {
                            if (allData) {
                                a(href = "../grabstätten/${graveSite.fileName}") { +"${graveSite.row}/${graveSite.place}" }
                            } else {
                                +"${graveSite.row}/${graveSite.place}"
                            }
                        }
                        td(classes = "left") {
                            +"${graveSite.type}"
                            if (graveSite.size > 1) {
                                +" (${graveSite.size})"
                            }
                        }
                        if (allData) {
                            td { +if (graveSite.validTo != null) dateFormat.format(graveSite.validTo) else ""  }
                            td(classes = "left") { +"${graveSite.owner?.firstName} ${graveSite.owner?.lastName}" }
                            td(classes = "left") { +graveSite.criterias.joinToString(" / ") }
                        }
                        td(classes = "left") {
                            +graveSite.graves
                                .filter { grave -> !grave.isEmpty() }
                                .joinToString(separator = ", ") { grave -> grave.deceased }
                        }
                    }
                }
            }
        }
    }

    private fun createSearchData(graveSite: GraveSite, allData: Boolean): String {
        return buildString {
            append(graveSite.field.uppercase())
            append(" ")
            append(graveSite.id.uppercase())
            append(" ")
            append(graveSite.name?.uppercase())
            append(" ")
            append(graveSite.place.uppercase())
            append(" ")
            append(graveSite.row.uppercase())
            append(" ")
            append(graveSite.type?.toString()?.uppercase())
            append(" ")
            append(graveSite.type?.toString()?.uppercase())
            append(" ")
            if (allData) {
                append(graveSite.criterias.joinToString(" ").uppercase())
                if (graveSite.validFrom != null) {
                    append(dateFormat.format(graveSite.validFrom))
                    append(" ")
                }
                if (graveSite.validTo != null) {
                    append(dateFormat.format(graveSite.validTo))
                    append(" ")
                }
                append(graveSite.owner?.firstName?.uppercase())
                append(" ")
                append(graveSite.owner?.lastName?.uppercase())
                append(" ")
                append(graveSite.owner?.street?.uppercase())
                append(" ")
                append(graveSite.owner?.zipAndTown?.uppercase())
                append(" ")
            }
            graveSite.graves
                .filter { grave -> !grave.isEmpty() }
                .forEach { grave ->
                    append(grave.deceased.uppercase())
                    append(" ")
                    if (grave.dateOfBirth != null) {
                        append(dateFormat.format(grave.dateOfBirth))
                        append(" ")
                    }
                    if (grave.dateOfDeath != null) {
                        append(dateFormat.format(grave.dateOfDeath))
                        append(" ")
                }
            }
        }
    }
}