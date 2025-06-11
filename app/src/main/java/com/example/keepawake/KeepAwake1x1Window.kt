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

        // Create a simple view (using TextView for visibility during testing)
        floatingView = TextView(context).apply {
            text = "•" // Small dot to make it visible for testing
            setBackgroundColor(0x80FF0000.toInt()) // Semi-transparent red for testing
            textSize = 8f
        }

        // Set up window parameters
        val params = WindowManager.LayoutParams().apply {
            // Window size - 1x1 pixel
            width = 100
            height = 100

            // Window type - overlay window
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            // Window flags
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or  // Makes it untouchable
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON  //Keep Screen on

            // Pixel format
            format = PixelFormat.TRANSLUCENT

            // Position - top-left corner
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

    fun makeInvisible() {
        floatingView?.apply {
            visibility = View.INVISIBLE
            // Alternative: set completely transparent background
            // setBackgroundColor(0x00000000)
        }
    }

    fun makeVisible() {
        floatingView?.apply {
            visibility = View.VISIBLE
            setBackgroundColor(0x80FF0000.toInt()) // Semi-transparent red
        }
    }

    fun updateSize(width: Int, height: Int) {
        if (!isShowing || floatingView == null) return

        val params = floatingView?.layoutParams as? WindowManager.LayoutParams
        params?.let {
            it.width = width
            it.height = height
            try {
                windowManager?.updateViewLayout(floatingView, it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updatePosition(x: Int, y: Int) {
        if (!isShowing || floatingView == null) return

        val params = floatingView?.layoutParams as? WindowManager.LayoutParams
        params?.let {
            it.x = x
            it.y = y
            try {
                windowManager?.updateViewLayout(floatingView, it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isWindowShowing(): Boolean = isShowing
}