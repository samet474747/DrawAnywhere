package com.shezik.drawanywhere.ui.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class RulerOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint().apply {
        color = Color.parseColor("#333333")
        strokeWidth = 3f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.parseColor("#555555")
        textSize = 28f
        isAntiAlias = true
    }

    private val highlightPaint = Paint().apply {
        color = Color.parseColor("#2196F3")
        strokeWidth = 5f
        isAntiAlias = true
    }

    var isHorizontal = true
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isHorizontal) {
            val yPos = height / 2f
            canvas.drawLine(0f, yPos, width.toFloat(), yPos, linePaint)

            var cm = 0
            var x = 0f
            val pxPerCm = 37.8f // Yaklaşık 1 cm piksel değeri

            while (x < width) {
                val tickHeight = if (cm % 5 == 0) 40f else 20f
                canvas.drawLine(x, yPos - tickHeight, x, yPos, linePaint)

                if (cm % 5 == 0) {
                    canvas.drawText("$cm", x + 5f, yPos - tickHeight - 10f, textPaint)
                }

                // Milimetre çizgileri
                for (mm in 1..9) {
                    val mmX = x + (mm * (pxPerCm / 10f))
                    if (mmX < width) {
                        val mmHeight = if (mm == 5) 30f else 15f
                        canvas.drawLine(mmX, yPos - mmHeight, mmX, yPos, linePaint)
                    }
                }

                x += pxPerCm
                cm++
            }
        } else {
            val xPos = width / 2f
            canvas.drawLine(xPos, 0f, xPos, height.toFloat(), linePaint)

            var cm = 0
            var y = 0f
            val pxPerCm = 37.8f

            while (y < height) {
                val tickWidth = if (cm % 5 == 0) 40f else 20f
                canvas.drawLine(xPos - tickWidth, y, xPos, y, linePaint)

                if (cm % 5 == 0) {
                    canvas.drawText("$cm", xPos + 10f, y + 25f, textPaint)
                }

                for (mm in 1..9) {
                    val mmY = y + (mm * (pxPerCm / 10f))
                    if (mmY < height) {
                        val mmWidth = if (mm == 5) 30f else 15f
                        canvas.drawLine(xPos - mmWidth, mmY, xPos, mmY, linePaint)
                    }
                }

                y += pxPerCm
                cm++
            }
        }
    }
}
