package de.hannisoft.graveplan.model

import java.util.*
import kotlin.math.abs

class GraveField(private val fieldName: String) {
    lateinit var field: PlanElement
    private val graveSites: MutableSet<GraveSite> = mutableSetOf()

    var rowCount: Int = -1
    var placeCount: Int = -1
    var deltaRow: Int = 0
    var deltaPlace: Int = 0
    private var finished: Boolean = false

    private lateinit var graveArray: Array<Array<Grave?>>

    fun finishEdit(fieldElements: Map<String, PlanElement>) {
        if (finished) return

        fillupMissingGraves()
        initMinMaxValues(fieldElements)
        fillGraveArray()
        setGraveClasses()
        finished = true
    }

    private fun fillupMissingGraves() {
        graveSites.forEach { graveSite ->
            val places = graveSite.graves.map { grave -> grave.id }.toSet()
            for (i in 0 until graveSite.rowSize) {
                for (j in 0 until graveSite.placeSize) {
                    try {
                        val grave = Grave(graveSite, graveSite.rowInt + i, graveSite.placeInt + j, "")
                        if (!places.contains(grave.id)) {
                            graveSite.graves.add(grave)
                        }
                    } catch (e: Exception) {
                        System.err.println("${e.javaClass.simpleName} while filling missing places of $graveSite into GraveField: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun initMinMaxValues(fieldElements: Map<String, PlanElement>) {
        val fieldElement = fieldElements[fieldName]
        if (fieldElement == null) {
            System.err.println("No field element found for field $fieldName")
            initMinMaxValuesFromGraves()
        } else {
            field = fieldElement
            rowCount = field.maxRow - field.minRow + if (field.minRow < 0) 0 else 1
            placeCount = field.maxPlace - field.minPlace + if (field.minPlace < 0) 0 else 1
            deltaRow = if (field.minRow < 0) abs(field.minRow) else 0
            deltaPlace = if (field.minPlace < 0) abs(field.minPlace) else 0
        }
        println("Initialized MinMaxValues of GraveField $field: rowCount=$rowCount / placeCount=$placeCount / $deltaRow / deltaPlace=$deltaPlace")
    }

    private fun initMinMaxValuesFromGraves() {
        var minRow = 1
        var maxRow = 1
        var minPlace = 1
        var maxPlace = 1

        for (graveSite in graveSites) {
            for (grave in graveSite.graves) {
                val row = grave.row
                minRow = minOf(minRow, row)
                maxRow = maxOf(maxRow, row)

                val plc = grave.place
                minPlace = minOf(minPlace, plc)
                maxPlace = maxOf(maxPlace, plc)
            }
        }

        rowCount = maxRow - minRow + if (minRow < 0) 0 else 1
        placeCount = maxPlace - minPlace + if (minPlace < 0) 0 else 1
        deltaRow = if (minRow < 0) abs(minRow) else 0
        deltaPlace = if (minPlace < 0) abs(minPlace) else 0

        field = PlanElement(this.hashCode(), "feld", minRow, maxRow, minPlace, maxPlace, fieldName)
    }

    private fun fillGraveArray() {
        graveArray = Array(rowCount) { arrayOfNulls<Grave>(placeCount) }
        for (graveSite in graveSites) {
            for (grave in graveSite.graves) {
                try {
                    val row = deltaRow + grave.row - if (grave.row > 0) 1 else 0
                    val plc = deltaPlace + grave.place - if (grave.place > 0) 1 else 0
                    graveArray[row][plc] = grave
                } catch (e: Exception) {
                    println("${e.javaClass.simpleName} while filling Grave $grave into GraveField: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setGraveClasses() {
        graveArray.forEachIndexed { i, graveRow ->
            graveRow.forEachIndexed { j, grave ->
                if (grave != null) {
                    checkBroached(grave)
                    checkStele(grave)
                    checkRef(grave)
                    checkNeighbors(grave, i, j)
                    addRuntime(grave)
                }
            }
        }
    }

    private fun checkBroached(grave: Grave) {
        val isBroached = grave.graveSite.isBroached()
        if (isBroached) {
            grave.addClass(GraveClass.BROACHED)
        } else if (!grave.isEmpty()) {
            grave.addClass(GraveClass.BUSY)
        }
        if (!isBroached && grave.isEmpty()) {
            grave.addClass(GraveClass.FREE)
        }
    }

    private fun checkStele(grave: Grave) {
        if (grave.graveSite.isStele()) {
            grave.addClass(GraveClass.STELE)
        }
    }

    private fun checkRef(grave: Grave) {
        if (grave.isRef()) {
            grave.addClass(GraveClass.REF)
        }
    }

    private fun checkNeighbors(grave: Grave, row: Int, place: Int) {
        if (!hasNeighborGraveInEqualGraveSite(row, place + 1, grave.graveSite)) {
            grave.addClass(GraveClass.W)
        }
        if (!hasNeighborGraveInEqualGraveSite(row, place - 1, grave.graveSite)) {
            grave.addClass(GraveClass.O)
        }
        if (!hasNeighborGraveInEqualGraveSite(row + 1, place, grave.graveSite)) {
            grave.addClass(GraveClass.N)
        }
        if (!hasNeighborGraveInEqualGraveSite(row - 1, place, grave.graveSite)) {
            grave.addClass(GraveClass.S)
        }
    }


    private fun addRuntime(grave: Grave?) {
        if (grave?.graveSite?.validFrom != null) {
            val validTo = Calendar.getInstance(Locale.GERMANY).apply {
                time = grave.graveSite.validTo
            }
            val now = Calendar.getInstance(Locale.GERMANY)
            val diff = validTo[Calendar.YEAR] - now[Calendar.YEAR]
            grave.runtimeYear = diff
        }
    }

    private fun hasNeighborGraveInEqualGraveSite(row: Int, place: Int, graveSite: GraveSite): Boolean {
        val neighbor =
            if (row in 0 until rowCount && place in 0 until placeCount) {
                graveArray[row][place]
            } else
                null
        return neighbor != null && neighbor.graveSite == graveSite
    }

    fun addGraveSite(graveSite: GraveSite) {
        graveSites.add(graveSite)
    }

    fun graveSites(): Set<GraveSite> = graveSites

    fun getFieldName(): String = field.name ?: fieldName

    fun getGrave(row: Int, place: Int): Grave? = graveArray[row][place]
}