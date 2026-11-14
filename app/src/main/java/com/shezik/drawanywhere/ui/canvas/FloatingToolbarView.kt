package com.shezik.drawanywhere.ui.canvas

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Button

class FloatingToolbarView(context: Context, private val onToolSelected: (String) -> Unit) : LinearLayout(context) {

    init {
        orientation = HORIZONTAL
        setBackgroundColor(Color.parseColor("#CC222222"))
        setPadding(16, 16, 16, 16)
        gravity = Gravity.CENTER

        val tools = listOf("Pen", "Eraser", "Shape", "Ruler", "Clear")

        for (tool in tools) {
            val btn = Button(context).apply {
                text = tool
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#444444"))
                setPadding(20, 10, 20, 10)
                setOnClickListener {
                    onToolSelected(tool)
                }
            }
            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0)
            }
            addView(btn, params)
        }
    }
}
