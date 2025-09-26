package de.hannisoft.graveplan.model

import java.util.*
import kotlin.math.abs

class GraveMap(private val fieldName: String) {
    private var field: PlanElement? = null
    private val graveSites: MutableSet<GraveSite> = mutableSetOf()

    private var rowCount: Int = -1
    private var placeCount: Int = -1
    private var deltaRow: Int = 0
    private var deltaPlace: Int = 0
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
            val places = graveSite.graves.map { it.id }.toSet()
            for (i in 0 until graveSite.rowSize) {
                for (j in 0 until graveSite.placeSize) {
                    try {
                        val grave = Grave(graveSite, graveSite.rowInt + i, graveSite.placeInt + j)
                        if (!places.contains(graveSite.id)) {
                            graveSite.graves.add(grave)
                        }
                    } catch (e: Exception) {
                        println("${e.javaClass.simpleName} while filling missing places of $graveSite into GraveMap: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun initMinMaxValues(fieldElements: Map<String, PlanElement>) {
        field = fieldElements[fieldName]
        if (field == null) {
            initMinMaxValuesFromGraves()
        } else {
            println("Found FieldElement of GraveMap $field")
            rowCount = field!!.maxRow - field!!.minRow + if (field!!.minRow < 0) 0 else 1
            placeCount = field!!.maxPlace - field!!.minPlace + if (field!!.minPlace < 0) 0 else 1
            deltaRow = if (field!!.minRow < 0) abs(field!!.minRow) else 0
            deltaPlace = if (field!!.minPlace < 0) abs(field!!.minPlace) else 0
            println("Initialized MinMaxValues of GraveMap $field: rowCount=$rowCount / placeCount=$placeCount / $deltaRow / deltaPlace=$deltaPlace")
        }
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
        println("Found MinMaxValues of GraveMap $field: Rows=[$minRow, $maxRow] Grave=[$minPlace, $maxPlace]")

        rowCount = maxRow - minRow + if (minRow < 0) 0 else 1
        placeCount = maxPlace - minPlace + if (minPlace < 0) 0 else 1
        deltaRow = if (minRow < 0) abs(minRow) else 0
        deltaPlace = if (minPlace < 0) abs(minPlace) else 0

        field = PlanElement(this.hashCode(), "feld", minRow, maxRow, minPlace, maxPlace, fieldName)
        println("Initialized MinMaxValues of GraveMap $field: rowCount=$rowCount / placeCount=$placeCount / $deltaRow / deltaPlace=$deltaPlace")
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
                    println("${e.javaClass.simpleName} while filling Grave $grave into GraveMap: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setGraveClasses() {
        graveArray.forEachIndexed { i, graveRow ->
            graveRow.forEachIndexed { j, grave ->
                try {
                    if (grave == null)
                        return@forEachIndexed

                    val graveSite = grave.graveSite

                    val isBroached = graveSite.isBroached()
                    if (isBroached) {
                        grave.addClass(GraveClass.BROACHED)
                    } else if (!grave.isEmpty()) {
                        grave.addClass(GraveClass.BUSY)
                    }

                    if (graveSite.isStele()) {
                        grave.addClass(GraveClass.STELE)
                    }

                    if (grave.isRef()) {
                        grave.addClass(GraveClass.REF)
                    }

                    if (!hasNeighborGraveInEqualGraveSite(i, j + 1, graveSite)) {
                        grave.addClass(GraveClass.W)
                    }
                    if (!hasNeighborGraveInEqualGraveSite(i, j - 1, graveSite)) {
                        grave.addClass(GraveClass.O)
                    }
                    if (!hasNeighborGraveInEqualGraveSite(i + 1, j, graveSite)) {
                        grave.addClass(GraveClass.N)
                    }
                    if (!hasNeighborGraveInEqualGraveSite(i - 1, j, graveSite)) {
                        grave.addClass(GraveClass.S)
                    }

                    if (!isBroached && grave.deceased == null) {
                        grave.addClass(GraveClass.FREE)
                    }

                    addRuntime(grave)
                } catch (e: Exception) {
                    println("${e.javaClass.simpleName} while initialising Grave's classes of $grave: ${e.message}")
                    e.printStackTrace()
                }
            }
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

    fun getFieldName(): String = field?.name ?: fieldName

    fun getField(): PlanElement? = field

    fun getGrave(row: Int, place: Int): Grave? = graveArray[row][place]
}