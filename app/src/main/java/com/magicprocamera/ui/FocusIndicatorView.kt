package com.magicprocamera.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class FocusIndicatorView(context: Context) : View(context) {
    private var focusX = 0f
    private var focusY = 0f
    private var isFocused = false
    private var focusAlpha = 0f
    private val focusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF2196F3.toInt()
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    private val focusCenterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF2196F3.toInt()
    }

    fun showPoint(x: Float, y: Float, focused: Boolean = false) {
        focusX = x
        focusY = y
        isFocused = focused
        focusAlpha = 1f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (focusAlpha <= 0f) return

        focusPaint.alpha = (focusAlpha * 255).toInt()
        focusCenterPaint.alpha = (focusAlpha * 255).toInt()

        val radius = 40f
        canvas.drawCircle(focusX, focusY, radius, focusPaint)
        canvas.drawCircle(focusX, focusY, 5f, focusCenterPaint)

        if (isFocused) {
            // Draw success indicator
            val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFF4CAF50.toInt()
                strokeWidth = 4f
                style = Paint.Style.STROKE
            }
            canvas.drawLine(focusX - 20, focusY, focusX - 5, focusY + 15, tickPaint)
            canvas.drawLine(focusX - 5, focusY + 15, focusX + 20, focusY - 10, tickPaint)
        }

        focusAlpha -= 0.02f
        if (focusAlpha > 0f) invalidate()
    }
}
