package com.example.keepawake

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView

class KeepAwake1x1Window(private val context: Context) {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var isShowing = false

    init {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun showFloatingWindow() {
        if (isShowing) return

        floatingView = TextView(context).apply {
            text = ""
            setBackgroundColor(0x01000000)
            textSize = 0f
        }

        val params = WindowManager.LayoutParams().apply {
            width = 1
            height = 1

            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

            format = PixelFormat.TRANSLUCENT

            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0
        }

        try {
            windowManager?.addView(floatingView, params)
            isShowing = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideFloatingWindow() {
        if (!isShowing || floatingView == null) return

        try {
            windowManager?.removeView(floatingView)
            isShowing = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}