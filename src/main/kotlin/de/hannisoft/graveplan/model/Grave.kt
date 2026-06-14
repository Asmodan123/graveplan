package de.hannisoft.graveplan.model

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

    fun getClassesString(): String =
        buildString {
            classes.forEach { append(it.toString().lowercase()).append(' ') }
            if (runtimeYear != 0) append("LZ$runtimeYear")
        }

    fun addClass(graveClass: GraveClass) {
        classes.add(graveClass)
    }

    fun isEmpty(): Boolean = deceased.trim().isEmpty()

    fun isRef(): Boolean = rowStr == graveSite.row && placeStr == graveSite.place

    override fun toString(): String = buildString {
        append(graveSite.field)
        append('/').append(row)
        append('/').append(place)
        append(' ').append(deceased)
        append(" ref=").append(graveSite)
    }
}