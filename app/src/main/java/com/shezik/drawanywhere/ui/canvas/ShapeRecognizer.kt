package com.shezik.drawanywhere.ui.canvas

import android.graphics.PointF
import kotlin.math.sqrt

object ShapeRecognizer {

    fun recognize(points: List<PointF>): List<PointF>? {
        if (points.size < 10) return null

        val start = points.first()
        val end = points.last()
        val totalDistance = distance(start, end)
        
        // Düz çizgi kontrolü
        val maxDeviation = points.maxOf { distanceToSegment(it, start, end) }
        if (maxDeviation < 15f) {
            return listOf(start, end)
        }

        // Kapalı şekil kontrolü (Başlangıç ve bitiş noktaları birbirine yakınsa)
        if (totalDistance < 40f && points.size > 15) {
            val minX = points.minOf { it.x }
            val maxX = points.maxOf { it.x }
            val minY = points.minOf { it.y }
            val maxY = points.maxOf { it.y }
            
            // Çizilen şeklin genişlik ve yükseklik oranına veya köşe sayısına göre üçgen/dikdörtgen ayrımı
            // Basitçe: Eğer kullanıcı üçgen çizdiyse, köşe analizi veya üst orta nokta tahmini yapabiliriz.
            // Şimdilik çizen kişinin çizim hatlarına göre en yüksek noktayı tepe kabul eden bir üçgen şablonu ekleyelim:
            val width = maxX - minX
            val height = maxY - minY
            
            // Eğer şekil kare/dikdörtgene yakın değil de üçgen formuna uygunsa (örneğin sivri tepe)
            // Alternatif olarak nokta sayısına göre üç köşe (Üçgen) tespiti:
            if (isTriangleCandidate(points)) {
                val topPoint = points.minByOrNull { it.y } ?: PointF(minX + width / 2f, minY)
                return listOf(
                    topPoint,
                    PointF(maxX, maxY),
                    PointF(minX, maxY),
                    topPoint
                )
            }

            // Standart Dikdörtgen
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

    private fun isTriangleCandidate(points: List<PointF>): Boolean {
        // En üstteki noktanın x eksenindeki konumu ile alt tabanın orta noktası kıyaslanabilir
        val minY = points.minOf { it.y }
        val topPoints = points.filter { it.y < minY + (points.maxOf { it.y } - minY) * 0.25f }
        // Eğer en üst kısım dar bir bölgede yoğunlaşıyorsa üçgendir
        return topPoints.size < points.size * 0.4f
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
