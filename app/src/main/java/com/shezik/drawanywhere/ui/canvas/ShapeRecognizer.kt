
package com.shezik.drawanywhere.ui.canvas

import android.graphics.Path
import android.graphics.PointF
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sqrt

class ShapeRecognizer {
    sealed class RecognizedShape {
        data class Line(val start: PointF, val end: PointF) : RecognizedShape()
        data class Rectangle(val left: Float, val top: Float, val right: Float, val bottom: Float) : RecognizedShape()
        data class Circle(val centerX: Float, val centerY: Float, val radius: Float) : RecognizedShape()
        data class Freehand(val path: Path) : RecognizedShape()
    }

    fun recognize(points: List<PointF>): RecognizedShape {
        if (points.size < 8) return createFreehandPath(points)
        val start = points.first()
        val end = points.last()
        val totalDistance = distance(start, end)
        var pathLength = 0f
        for (i in 0 until points.size - 1) {
            pathLength += distance(points[i], points[i + 1])
        }
        if (isStraightLine(points, start, end)) return RecognizedShape.Line(start, end)
        val isClosed = totalDistance < (pathLength * 0.25f)
        if (isClosed && points.size > 15) {
            val minX = points.minOf { it.x }
            val maxX = points.maxOf { it.x }
            val minY = points.minOf { it.y }
            val maxY = points.maxOf { it.y }
            val width = maxX - minX
            val height = maxY - minY
            val cx = minX + width / 2f
            val cy = minY + height / 2f
            val radius = (width + height) / 4f
            return if (isCircle(points, cx, cy, radius)) {
                RecognizedShape.Circle(cx, cy, radius)
            } else {
                RecognizedShape.Rectangle(minX, minY, maxX, maxY)
            }
        }
        return createFreehandPath(points)
    }

    private fun distance(p1: PointF, p2: PointF) = hypot((p2.x - p1.x).toDouble(), (p2.y - p1.y).toDouble()).toFloat()
    private fun isStraightLine(points: List<PointF>, start: PointF, end: PointF): Boolean {
        val lineDist = distance(start, end)
        if (lineDist < 30f) return false
        var maxDev = 0f
        for (p in points) { maxDev = max(maxDev, perpDist(p, start, end)) }
        return maxDev < (lineDist * 0.12f)
    }
    private fun perpDist(p: PointF, s: PointF, e: PointF): Float {
        val len = distance(s, e)
        if (len == 0f) return distance(p, s)
        return abs((e.y - s.y) * p.x - (e.x - s.x) * p.y + e.x * s.y - e.y * s.x) / len
    }
    private fun isCircle(points: List<PointF>, cx: Float, cy: Float, r: Float): Boolean {
        if (r < 20f) return false
        var sum = 0f
        for (p in points) {
            val diff = distance(p, PointF(cx, cy)) - r
            sum += diff * diff
        }
        return sqrt((sum / points.size).toDouble()) < (r * 0.25)
    }
    private fun createFreehandPath(points: List<PointF>): RecognizedShape.Freehand {
        val path = Path()
        if (points.isNotEmpty()) {
            path.moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) path.lineTo(points[i].x, points[i].y)
        }
        return RecognizedShape.Freehand(path)
    }
}
