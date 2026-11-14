package com.shezik.drawanywhere.ui.canvas

import android.graphics.PointF
import kotlin.math.sqrt

object ShapeRecognizer {

    fun recognize(points: List<PointF>): List<PointF>? {
        if (points.size < 10) return null

        val start = points.first()
        val end = points.last()
        val totalDistance = distance(start, end)
        
        // Çizgi kontrolü (Başlangıç ve bitiş dışındaki noktalar çok uzaklaşmadıysa düz çizgidir)
        val maxDeviation = points.maxOf { distanceToSegment(it, start, end) }
        if (maxDeviation < 15f) {
            return listOf(start, end)
        }

        // Kapalı şekil kontrolü (Başlangıç ve bitiş noktaları birbirine yakınsa)
        if (totalDistance < 40f && points.size > 20) {
            val minX = points.minOf { it.x }
            val maxX = points.maxOf { it.x }
            val minY = points.minOf { it.y }
            val maxY = points.maxOf { it.y }
            
            // Diktörtgen / Kare tahmini için köşe noktaları oluştur
            return listOf(
                PointF(minX, minY),
                PointF(maxX, minY),
                PointF(maxX, maxY),
                PointF(minX, maxY),
                PointF(minX, minY)
            )
        }

        return null
    }

    private fun distance(p1: PointF, p2: PointF): Float {
        return sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y))
    }

    private fun distanceToSegment(p: PointF, p1: PointF, p2: PointF): Float {
        val l2 = distanceSquared(p1, p2)
        if (l2 == 0f) return distance(p, p1)
        var t = ((p.x - p1.x) * (p2.x - p1.x) + (p.y - p1.y) * (p2.y - p1.y)) / l2
        t = t.coerceIn(0f, 1f)
        val projection = PointF(p1.x + t * (p2.x - p1.x), p1.y + t * (p2.y - p1.y))
        return distance(p, projection)
    }

    private fun distanceSquared(p1: PointF, p2: PointF): Float {
        return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)
    }
}
