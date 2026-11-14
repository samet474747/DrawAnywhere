package com.shezik.drawanywhere.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import com.shezik.drawanywhere.ui.canvas.FloatingToolbarView
import com.shezik.drawanywhere.ui.canvas.RulerOverlayView

class DrawingOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingToolbar: FloatingToolbarView? = null
    private var rulerOverlay: RulerOverlayView? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Kayan Araç Çubuğu Ekleniyor
        floatingToolbar = FloatingToolbarView(this) { tool ->
            when (tool) {
                "Ruler" -> toggleRuler()
                "Clear" -> {}
            }
        }

        val toolbarParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        windowManager.addView(floatingToolbar, toolbarParams)
    }

    private fun toggleRuler() {
        if (rulerOverlay == null) {
            rulerOverlay = RulerOverlayView(this)
            val rulerParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            )
            windowManager.addView(rulerOverlay, rulerParams)
        } else {
            windowManager.removeView(rulerOverlay)
            rulerOverlay = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingToolbar?.let { windowManager.removeView(it) }
        rulerOverlay?.let { windowManager.removeView(it) }
    }
}
