package de.hannisoft.graveplan.model

import java.util.*

class GraveSite(val field: String, val row: String, val place: String) {
    companion object {
        const val NAME_SIZE = "plangröße="
        const val CRITERIA_GERAUEMT = "abgeräumt"
        const val CRITERIA_STELE = "Stele"
        private val NAME_SIZE_LEN = NAME_SIZE.length
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
        set(value) {
            field = value
            if (value != null) {
                try {
                    val pos = value.lowercase(Locale.getDefault()).indexOf(NAME_SIZE)
                    if (pos > -1) {
                        rowSize = value.substring(pos + NAME_SIZE_LEN, pos + NAME_SIZE_LEN + 1).toInt()
                        placeSize = value.substring(pos + NAME_SIZE_LEN + 1, pos + NAME_SIZE_LEN + 2).toInt()
                    }
                } catch (e: Exception) {
                    println("Invalid GraveName '$value': ${e.message}")
                    return
                }
            }
            if (rowSize == 0 || placeSize == 0) {
                println("Invalid GraveName '$value' on $this")
            }
        }

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
        criterias.add(crit)
    }

    fun isBroached(): Boolean = criterias.contains(CRITERIA_GERAUEMT)

    fun isStele(): Boolean = criterias.contains(CRITERIA_STELE)
}