package com.magicprocamera.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.math.max
import kotlin.math.min

class HistogramView(context: Context) : View(context) {
    private val bins = IntArray(64)
    private val backgroundPaint = Paint().apply { color = 0xCC1A1A1A.toInt() }
    private val histogramPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFF9800.toInt()
        strokeWidth = 2f
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 10f
    }

    fun pushLuma(luma: Int) {
        for (i in bins.indices) bins[i] = max(0, bins[i] - 2)
        val idx = (luma.coerceIn(0, 255) * bins.size / 256).coerceIn(0, bins.lastIndex)
        bins[idx] = min(100, bins[idx] + 20)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        val barWidth = width / bins.size.toFloat()
        val bottom = height - 8f
        for (i in bins.indices) {
            val barHeight = bins[i] * height / 100f
            canvas.drawLine(
                i * barWidth + barWidth / 2,
                bottom,
                i * barWidth + barWidth / 2,
                bottom - barHeight,
                histogramPaint
            )
        }
        canvas.drawText("Luma", 4f, 12f, textPaint)
    }
}
