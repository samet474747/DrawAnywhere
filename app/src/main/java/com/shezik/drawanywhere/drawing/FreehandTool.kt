package com.shezik.drawanywhere.drawing

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import com.shezik.drawanywhere.model.DrawAction
import com.shezik.drawanywhere.model.Stroke
import com.shezik.drawanywhere.ui.canvas.ShapeRecognizer

class FreehandTool(private val ctx: ToolContext) : StrokeTool {

    private val rawPoints = mutableListOf<PointF>()

    override fun onStart(point: Offset) {
        rawPoints.clear()
        rawPoints.add(PointF(point.x, point.y))
        ctx.strokes.add(Stroke(
            _points = mutableListOf(point),
            color = ctx.penConfig.color,
            width = ctx.penConfig.width,
            alpha = ctx.penConfig.alpha,
            penType = ctx.penConfig.penType,
        ))
    }

    override fun onMove(point: Offset) {
        rawPoints.add(PointF(point.x, point.y))
        ctx.strokes.lastOrNull()?.let { stroke ->
            stroke._points.add(point)
            ctx.notifyChanged()
        }
    }

    override fun onFinish() {
        val stroke = ctx.strokes.lastOrNull() ?: return
        if (rawPoints.size > 5) {
            val recognized = ShapeRecognizer.recognize(rawPoints)
            if (recognized != null) {
                stroke._points.clear()
                stroke._points.addAll(recognized.map { Offset(it.x, it.y) })
            }
        }
        ctx.pushUndo(DrawAction.AddStroke(stroke))
        ctx.notifyChanged()
    }
}
