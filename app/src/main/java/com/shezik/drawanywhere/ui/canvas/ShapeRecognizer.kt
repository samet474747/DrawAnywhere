package com.shezik.drawanywhere.ui.canvas

import android.graphics.PointF
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sqrt

object ShapeRecognizer {

    fun recognize(points: List<PointF>): List<PointF>? {
        if (points.size < 12) return null

        val start = points.first()
        val end = points.last()
        val totalDistance = distance(start, end)

        // 1. DÜZ ÇİZGİ KONTROLÜ
        val maxDeviation = points.maxOf { distanceToSegment(it, start, end) }
        if (maxDeviation < 15f) {
            return listOf(start, end)
        }

        // Sınır kutusu (Bounding Box) hesaplamaları
        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }
        val width = maxX - minX
        val height = maxY - minY

        // 2. ÇEMBER / DAİRE KONTROLÜ
        // Başlangıç ve bitiş yakınsa, çevre ile alan oranı dairesel form veriyorsa çemberdir
        if (totalDistance < 50f && isCircleShape(points, minX, maxX, minY, maxY)) {
            val centerX = (minX + maxX) / 2f
            val centerY = (minY + maxY) / 2f
            val radius = (width + height) / 4f
            return generateCirclePoints(centerX, centerY, radius)
        }

        // 3. KAPALI ŞEKİLLER (Üçgenler ve Dikdörtgenler)
        if (totalDistance < 60f) {
            // Köşe tespiti yapalım
            val corners = findCorners(points)

            when (corners.size) {
                3 -> {
                    // Üçgen (Dar açılı, geniş açılı veya dik üçgen - orijinal köşe noktalarını koruyarak düzeltir)
                    return listOf(corners[0], corners[1], corners[2], corners[0])
                }
                4, 5 -> {
                    // Kare veya Dikdörtgen
                    return listOf(
                        PointF(minX, minY),
                        PointF(maxX, minY),
                        PointF(maxX, maxY),
                        PointF(minX, maxY),
                        PointF(minX, minY)
                    )
                }
            }
        }

        return null
    }

    private fun isCircleShape(points: List<PointF>, minX: Float, maxX: Float, minY: Float, maxY: Float): Boolean {
        val centerX = (minX + maxX) / 2f
        val centerY = (minY + maxY) / 2f
        val radius = ((maxX - minX) + (maxY - minY)) / 4f
        if (radius < 10f) return false

        var deviationSum = 0f
        for (p in points) {
            val dist = distance(p, PointF(centerX, centerY))
            deviationSum += abs(dist - radius)
        }
        val avgDeviation = deviationSum / points.size
        return avgDeviation < (radius * 0.25f) // Çember sapma toleransı
    }

    private fun generateCirclePoints(cx: Float, cy: Float, r: Float): List<PointF> {
        val circlePoints = mutableListOf<PointF>()
        val segments = 36
        for (i in 0..segments) {
            val angle = (i * 2 * Math.PI / segments).toFloat()
            circlePoints.add(PointF(cx + r * kotlin.math.cos(angle), cy + r * kotlin.math.sin(angle)))
        }
        return circlePoints
    }

    private fun findCorners(points: List<PointF>): List<PointF> {
        // Basitleştirilmiş köşe bulma (Douglas-Peucker türevi köşe elemesi)
        val corners = mutableListOf<PointF>()
        if (points.isEmpty()) return corners

        corners.add(points.first())
        var farthestPoint: PointF? = null
        var maxDist = 0f

        val start = points.first()
        val end = points.last()

        for (p in points) {
            val d = distanceToSegment(p, start, end)
            if (d > maxDist) {
                maxDist = d
                farthestPoint = p
            }
        }

        farthestPoint?.let { corners.add(it) }
        
        // Eğer karmaşık bir formse ek köşe noktaları ekle
        if (points.size > 30) {
            val midIndex = points.size / 2
            corners.add(points[midIndex])
        }

        corners.add(points.last())
        return corners.distinctBy { "${it.x.toInt() / 15},${it.y.toInt() / 15}" }
    }

    private fun distance(p1: PointF, p2: PointF): Float {
        return hypot((p2.x - p1.x).toDouble(), (p2.y - p1.y).toDouble()).toFloat()
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
