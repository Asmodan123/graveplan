package de.hannisoft.graveplan.model

import java.util.*

class GraveSite(val field: String, val row: String, val place: String) {
    companion object {
        const val CRITERIA_PLAN_SIZE = "plangröße="
        const val CRITERIA_GERAUEMT = "abgeräumt"
        const val CRITERIA_STELE = "Stele"
        fun createId(field: String, row: String, place: String): String = "$field/$row/$place"
    }

    val rowInt: Int = row.replace(Regex("[^0-9\\-]"), "").toInt()
    val placeInt: Int = place.replace(Regex("[^0-9\\-]"), "").toInt()
    val id: String = createId(field, row, place)
    val fileName: String = "${field}_${row}_${place}.html"

    var type: GraveSiteType? = null
    var typeAsString: String? = null
        set(value) {
            type = GraveSiteType.getTypeByName(value)
            field = value
        }

    var name: String? = null

    var validFrom: Date? = null
    var validTo: Date? = null
    var owner: Owner? = null
    var size: Int = 0
    var rowSize: Int = 0
    var placeSize: Int = 0

    val graves: MutableList<Grave> = mutableListOf()
    var criterias: MutableList<String> = mutableListOf()

    override fun toString(): String = " $id"

    fun addCriteria(crit: String?) {
        if (crit.isNullOrBlank()) return
        if (crit.lowercase(Locale.getDefault()).startsWith(CRITERIA_PLAN_SIZE)) {
            // e.g. "PlanGröße=12" -> 1 row, 2 places; only used for the plan layout, not shown as criteria
            setPlanSize(crit)
            return
        }
        criterias.add(crit)
    }

    fun hasPlanSize(): Boolean = rowSize > 0 && placeSize > 0

    private fun setPlanSize(crit: String) {
        val value = crit.substring(CRITERIA_PLAN_SIZE.length).trim()
        if (value.length != 2 || !value.all { it.isDigit() } || value.contains('0')) {
            System.err.println("Invalid criteria '$crit' on grave site $id: expected two digits (rows, places) greater than 0")
            return
        }
        rowSize = value[0].digitToInt()
        placeSize = value[1].digitToInt()
    }

    fun isBroached(): Boolean = criterias.contains(CRITERIA_GERAUEMT)

    fun isStele(): Boolean = criterias.contains(CRITERIA_STELE)
}