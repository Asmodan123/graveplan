package de.hannisoft.graveplan.model

class PlanElement(val id: Int, elementType: String, var minRow: Int = 0, var maxRow: Int = 0, var minPlace: Int = 0, var maxPlace: Int = 0, var name: String?
= null, var title: String? = null) {
    enum class DeltaType { X, Y }
    enum class EdgeType { TOP, LEFT, BOTTOM, RIGHT }
    enum class CornerPointType { TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT }

    val type: ElementType = ElementType.getTypeByName(elementType)
    private val points: MutableList<Point> = mutableListOf()
    private val cornerPoints: MutableMap<CornerPointType, Point> = mutableMapOf()

    fun getPoints(): List<Point> = points

    fun getXs(): IntArray = points.map { it.x }.toIntArray()
    fun getYs(): IntArray = points.map { it.y }.toIntArray()

    fun addPoint(point: Point) {
        points.add(point)
    }

    fun getCornerPoint(cornerPoint: CornerPointType): Point? {
        if (cornerPoints.isEmpty() && points.isNotEmpty()) {
            initCornerPoints()
        }
        return cornerPoints[cornerPoint]
    }

    private fun initCornerPoints() {
        cornerPoints.clear()
        val sorted = points.sortedBy { it.x }

        val (p1, p2) = sorted.take(2)
        cornerPoints[CornerPointType.BOTTOM_LEFT] = if (p1.y > p2.y) p1 else p2
        cornerPoints[CornerPointType.TOP_LEFT] = if (p1.y > p2.y) p2 else p1

        val (q1, q2) = sorted.takeLast(2)
        cornerPoints[CornerPointType.BOTTOM_RIGHT] = if (q1.y > q2.y) q1 else q2
        cornerPoints[CornerPointType.TOP_RIGHT] = if (q1.y > q2.y) q2 else q1

        println("TOP_LEFT: ${cornerPoints[CornerPointType.TOP_LEFT]}")
        println("BOTTOM_LEFT: ${cornerPoints[CornerPointType.BOTTOM_LEFT]}")
        println("TOP_RIGHT: ${cornerPoints[CornerPointType.TOP_RIGHT]}")
        println("BOTTOM_RIGHT: ${cornerPoints[CornerPointType.BOTTOM_RIGHT]}")
    }

    fun getDelta(deltaType: DeltaType, edge: EdgeType): Float {
        val (cp1, cp2) = when (edge) {
            EdgeType.TOP -> CornerPointType.TOP_RIGHT to CornerPointType.TOP_LEFT
            EdgeType.BOTTOM -> CornerPointType.BOTTOM_RIGHT to CornerPointType.BOTTOM_LEFT
            EdgeType.LEFT -> CornerPointType.BOTTOM_LEFT to CornerPointType.TOP_LEFT
            EdgeType.RIGHT -> CornerPointType.BOTTOM_RIGHT to CornerPointType.TOP_RIGHT
        }

        val p1 = getCornerPoint(cp1) ?: return 0f
        val p2 = getCornerPoint(cp2) ?: return 0f

        return when (deltaType) {
            DeltaType.X -> (p1.x - p2.x).toFloat() / getPlaceCount()
            DeltaType.Y -> (p1.y - p2.y).toFloat() / getRowCount()
        }
    }

    fun getPlaceCount(): Int = maxPlace - minPlace + 1
    fun getRowCount(): Int = maxRow - minRow + 1

    fun getLabel(): String = buildString {
        append(name ?: "")
        title?.let { append(" ").append(it) }
    }

    override fun toString(): String =
        "$type $name [$minRow, $maxRow] / [$minPlace, $maxPlace]"
}