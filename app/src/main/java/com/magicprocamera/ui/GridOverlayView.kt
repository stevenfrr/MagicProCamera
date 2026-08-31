package com.magicprocamera.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class GridOverlayView(context: Context) : View(context) {
    private var mode = ModernCameraUI.GridMode.NONE
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x66FFFFFF.toInt()
        strokeWidth = 1f
    }

    fun setMode(m: ModernCameraUI.GridMode) {
        mode = m
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (mode) {
            ModernCameraUI.GridMode.RULE_OF_THIRDS -> drawRuleOfThirds(canvas)
            ModernCameraUI.GridMode.GRID_4X4 -> drawGrid4x4(canvas)
            ModernCameraUI.GridMode.DIAGONALS -> drawDiagonals(canvas)
            ModernCameraUI.GridMode.NONE -> {}
        }
    }

    private fun drawRuleOfThirds(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        canvas.drawLine(w / 3f, 0f, w / 3f, h, linePaint)
        canvas.drawLine(2f * w / 3f, 0f, 2f * w / 3f, h, linePaint)
        canvas.drawLine(0f, h / 3f, w, h / 3f, linePaint)
        canvas.drawLine(0f, 2f * h / 3f, w, 2f * h / 3f, linePaint)
    }

    private fun drawGrid4x4(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        for (i in 1..3) {
            canvas.drawLine(i * w / 4f, 0f, i * w / 4f, h, linePaint)
            canvas.drawLine(0f, i * h / 4f, w, i * h / 4f, linePaint)
        }
    }

    private fun drawDiagonals(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        canvas.drawLine(0f, 0f, w, h, linePaint)
        canvas.drawLine(w, 0f, 0f, h, linePaint)
    }
}
