package de.hannisoft.de.hannisoft.graveplan.writer

import de.hannisoft.graveplan.model.GraveMap
import de.hannisoft.graveplan.model.PlanElement
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.io.File
import java.text.SimpleDateFormat

class HTMLGraveMapWriter(outputDirString: String, val outputType: OutputType) {
    val outputDir = File(outputDirString,
        if (outputType == OutputType.REFERENCE) "belegung" else "laufzeit")
    val dateFormat = SimpleDateFormat(
        if (outputType == OutputType.REFERENCE) "dd.MM.yyyy" else "yyyy")

    init {
        initOutputDir(outputDir)
        writeStyleCss(outputDir)
    }

    fun write(graveMap: GraveMap, dueDay: String, allData: Boolean) {
        val html = createHTML().html {
            writeHeader(graveMap.field)
            body {
                writeNavBar(graveMap.field, dueDay, allData)
                table {
                    style = "width: ${graveMap.placeCount * 140 + 100}px; table-layout: fixed"
                    writeTableHeader(graveMap)
                    writeTableBody(graveMap, allData)
                    writeTableFooter(graveMap)
                }
            }
        }
        File(outputDir, "${graveMap.getFieldName()}.html").writeText(html)

    }

    private fun writeStyleCss(outputDir: File) {
        val additionalContent = buildString {
            append("\n")
            if (outputType == OutputType.REFERENCE) {
                append("td.busy {background-color: #F3F781}\n")
                append("td.free {background-color: #B3FFB3}\n")
                append("td.ref {background-color: #81BEF7}\n")
                append("td.broached {background-color: #FFA500}\n")
                append("td.stele {background-color: #FF0000}\n")
            } else {
                for (i in 0..36) {
                    append("td.LZ$i {background-color: #FFFF")
                    append(String.format("%02x", 255 - i * 7).uppercase())
                    append("}\n")
                }
            }
        }
        FileExporter().exportFile(outputDir, "style.css", additionalContent)
    }

    private fun HTML.writeHeader(field: PlanElement) {
        head {
            title("Feld ${field.getLabel()}")
            link { rel = "stylesheet"; href = "style.css"; type = "text/css" }
            link { rel = "stylesheet"; href = "../plan/nav.css"; type = "text/css" }
        }
    }

    private fun BODY.writeNavBar(field: PlanElement, dueDay: String, allData: Boolean) {
        ul {
            li { a(href = "../Friedhofsplan.html") { +"Übersicht" } }
            li { a(href = "../suche/Suche.html") { +"Suche" } }
            if (outputType == OutputType.REFERENCE) {
                li { a(href = "#", classes = "active") { +"Belegung - Feld ${field.name}" } }
                if (allData) {
                    li { a(href = "../laufzeit/${field.name}.html") { +"Restlaufzeit - Feld ${field.name}" } }
                }
            } else {
                li { a(href = "../belegung/${field.name}.html") { +"Belegung - Feld ${field.name}" } }
                li { a(href = "#", classes = "active") { +"Restlaufzeit - Feld ${field.name}" } }
            }
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

    private fun TABLE.writeTableHeader(graveMap: GraveMap) {
        thead {
            tr {
                th(classes = "col") {
                    +"Platz →"
                    br
                    +"Reihe ↓"
                }
                for (i in graveMap.placeCount - 1 downTo 0 ) {
                    var place = i - graveMap.deltaPlace
                    if (place >= 0)
                        place++
                    th { +String.format("%02d", place) }
                }
                th(classes = "col") {
                    +"← Platz"
                    br
                    +"↓ Reihe"
                }
            }
        }
    }

    private fun TABLE.writeTableBody(graveMap: GraveMap, allData: Boolean) {
        tbody {
            for (i in graveMap.rowCount - 1 downTo 0) {
                var row = i - graveMap.deltaRow
                if (row >= 0) {
                    row++
                }
                tr {
                    td(classes = "reihe") { +String.format("%02d", row) }
                    for (j in graveMap.placeCount - 1 downTo 0) {
                        val grave = graveMap.getGrave(i, j)
                        if (grave == null) {
                            td { }
                        } else {
                            td(classes = grave.getClassesString()) {
                                id = if (grave.isRef()) grave.graveSite.id.replace('/','_') else ""
                                style = "cursor:pointer"
                                onClick = "location.href='../grabstätten/${grave.graveSite.fileName}'"
                                if (allData) {
                                    a(href = "../grabstätten/${grave.graveSite.fileName}") {
                                        style = "text-decoration:none;color:black"
                                        div {
                                            style = "display: block;"
                                            +grave.deceased
                                            if (outputType == OutputType.REFERENCE) {
                                                if (grave.dateOfBirth != null) {
                                                    br
                                                    +"* ${dateFormat.format(grave.dateOfBirth)}"
                                                }
                                                if (grave.dateOfDeath != null) {
                                                    br
                                                    +"+ ${dateFormat.format(grave.dateOfDeath)}"
                                                }
                                            } else {
                                                if (grave.graveSite.validTo != null) {
                                                    br
                                                    +"bis ${dateFormat.format(grave.graveSite.validTo)}"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    td(classes = "reihe") { +String.format("%02d", row) }
                }
            }
        }
    }

    private fun TABLE.writeTableFooter(graveMap: GraveMap) {
        tfoot {
            tr {
                th(classes = "reihe") {
                    +"Reihe ↑"
                    br
                    +"Platz →"
                }
                for (i in graveMap.placeCount - 1 downTo 0 ) {
                    var place = i - graveMap.deltaPlace
                    if (place >= 0)
                        place++
                    th { +String.format("%02d", place) }
                }
                th(classes = "col") {
                    +"↑ Reihe"
                    br
                    +"← Platz"
                }
            }
        }
    }
}

enum class OutputType {
    REFERENCE, RUNTIME
}