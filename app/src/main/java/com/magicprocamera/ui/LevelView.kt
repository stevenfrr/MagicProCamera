package com.magicprocamera.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.math.abs

class LevelView(context: Context) : View(context) {
    private var roll = 0f
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x99FFFFFF.toInt()
        strokeWidth = 2f
    }
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF4CAF50.toInt()
        strokeWidth = 3f
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 12f
    }

    fun setRoll(r: Float) {
        roll = r.coerceIn(-45f, 45f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f

        // Reference line (horizontal)
        canvas.drawLine(centerX - 60, centerY, centerX + 60, centerY, linePaint)

        // Level indicator line (rotated)
        val len = 60f
        val rad = Math.toRadians(roll.toDouble()).toFloat()
        val dx = (len * kotlin.math.cos(rad.toDouble())).toFloat()
        val dy = (len * kotlin.math.sin(rad.toDouble())).toFloat()
        canvas.drawLine(centerX - dx, centerY - dy, centerX + dx, centerY + dy, centerPaint)

        // Center point
        canvas.drawCircle(centerX, centerY, 4f, centerPaint)

        // Level indicator text
        val levelText = if (abs(roll) < 2f) "✓" else String.format("%.1f°", roll)
        canvas.drawText(levelText, centerX - 20, centerY - 15, textPaint)
    }
}
