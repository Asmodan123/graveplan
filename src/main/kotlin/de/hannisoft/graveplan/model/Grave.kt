package de.hannisoft.graveplan.model

import java.text.SimpleDateFormat
import java.util.Date

class Grave(
    val graveSite: GraveSite,
    val row: Int,
    val place: Int,
    var deceased: String
) {
    var dateOfBirth: Date? = null
    var dateOfDeath: Date? = null
    private val classes: MutableSet<GraveClass> = mutableSetOf()
    var runtimeYear: Int = 0
    val rowStr: String = "%02d".format(row)
    val placeStr: String = "%02d".format(place)
    val id: String = "$rowStr/$placeStr"

    fun getRowInt(): Int = row
    fun getPlaceInt(): Int = place

    fun getClasses(): Set<GraveClass> = classes

    fun getClassesString(): String = buildString {
        classes.forEach { append(it.toString().lowercase()).append(' ') }
        if (runtimeYear != 0) append("LZ$runtimeYear")
    }

    fun addClass(graveClass: GraveClass) {
        classes.add(graveClass)
    }

    fun isEmpty(): Boolean = deceased.trim().isEmpty()

    fun isRef(): Boolean = rowStr == graveSite.row && placeStr == graveSite.place

    fun getReference(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val owner = graveSite.owner

        val criteriasString =
            if (graveSite.criterias.isNotEmpty()) {
                graveSite.criterias.joinToString(", ", prefix = ";")
            } else
                ""

        val validToString = graveSite.validTo?.let { ";Nutzungsrecht bis ${dateFormat.format(it)}" } ?: ""

        val ownerString = owner?.let { "${it.firstName} ${it.lastName};${it.street};${it.zipAndTown}" } ?: ""

        return "Gabstätte: ${graveSite.id}" +
                ";===================${criteriasString}${validToString}" +
                ";${graveSite.name}" +
                ";;Nutzungsberechtigter:" +
                ";-----------------------------------;${ownerString}"
    }

    override fun toString(): String = buildString {
        append(graveSite.field)
        append('/').append(row)
        append('/').append(place)
        append(' ').append(deceased)
        append(" ref=").append(graveSite)
    }
}