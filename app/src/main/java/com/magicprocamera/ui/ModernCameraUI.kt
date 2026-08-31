package com.magicprocamera.ui

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.abs

class ModernCameraUI(context: Context) : FrameLayout(context) {
    private val root = FrameLayout(context)
    private val topBar = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        setBackgroundColor(0x1A000000.toInt())
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 56).apply {
            gravity = Gravity.TOP
        }
    }
    private val centerOverlay = FrameLayout(context).apply {
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }
    private val bottomBar = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        setBackgroundColor(0x1A000000.toInt())
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 96).apply {
            gravity = Gravity.BOTTOM
        }
        gravity = Gravity.CENTER
    }

    // Top indicators
    val aiIndicator = TextView(context).apply {
        text = "✨ IA"
        setTextColor(Color.WHITE)
        textSize = 12f
        setPadding(8, 0, 8, 0)
    }
    val sceneLabel = TextView(context).apply {
        text = "Scène"
        setTextColor(Color.WHITE)
        textSize = 12f
        setPadding(8, 0, 8, 0)
    }
    val flashIndicator = TextView(context).apply {
        text = "🔦"
        setTextColor(Color.WHITE)
        textSize = 14f
        setPadding(8, 0, 8, 0)
    }
    val hdrIndicator = TextView(context).apply {
        text = "HDR"
        setTextColor(Color.WHITE)
        textSize = 12f
        setPadding(8, 0, 8, 0)
        alpha = 0.5f
    }
    val stabilizationIndicator = TextView(context).apply {
        text = "OIS"
        setTextColor(Color.WHITE)
        textSize = 12f
        setPadding(8, 0, 8, 0)
        alpha = 0.5f
    }

    // Bottom controls
    val photoModeBtn = createBottomButton("📷 PHOTO")
    val proModeBtn = createBottomButton("⚙️ PRO")
    val aiAutoBtn = createBottomButton("✨ IA")
    val galleryBtn = createBottomButton("📁")
    val captureBtn = createCaptureButton()
    val settingsBtn = createBottomButton("⚙️")
    val switchCameraBtn = createBottomButton("🔄")

    // Overlays
    val gridOverlay = GridOverlayView(context).apply {
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }
    val histogramOverlay = HistogramView(context).apply {
        layoutParams = FrameLayout.LayoutParams(240, 80).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            rightMargin = 16
            bottomMargin = 120
        }
    }
    val levelOverlay = LevelView(context).apply {
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 60).apply {
            gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            topMargin = 100
        }
    }
    val focusIndicator = FocusIndicatorView(context).apply {
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    // Pro mode controls
    val isoSlider = createSliderRow("ISO", "100 - 3200")
    val shutterSlider = createSliderRow("Vitesse", "1/1000 - 1s")
    val evSlider = createSliderRow("EV", "-2 ... +2")
    val wbSlider = createSliderRow("WB", "3000K - 7500K")
    val focusSlider = createSliderRow("Focus", "AUTO - MANUEL")

    private val proControlsPanel = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(0x80000000.toInt())
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM
        }
        visibility = GONE
    }

    init {
        setBackgroundColor(Color.BLACK)
        addView(root)

        // Top bar
        topBar.addView(aiIndicator)
        topBar.addView(sceneLabel)
        topBar.addView(Space(context).apply { layoutParams = LinearLayout.LayoutParams(0, 0, 1f) })
        topBar.addView(flashIndicator)
        topBar.addView(hdrIndicator)
        topBar.addView(stabilizationIndicator)
        root.addView(topBar)

        // Center overlays
        centerOverlay.addView(gridOverlay)
        centerOverlay.addView(levelOverlay)
        centerOverlay.addView(focusIndicator)
        centerOverlay.addView(histogramOverlay)
        root.addView(centerOverlay)

        // Pro controls panel
        proControlsPanel.addView(isoSlider)
        proControlsPanel.addView(shutterSlider)
        proControlsPanel.addView(evSlider)
        proControlsPanel.addView(wbSlider)
        proControlsPanel.addView(focusSlider)
        root.addView(proControlsPanel)

        // Bottom bar
        bottomBar.addView(galleryBtn.apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
        bottomBar.addView(switchCameraBtn.apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
        bottomBar.addView(captureBtn.apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
        bottomBar.addView(photoModeBtn.apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
        bottomBar.addView(proModeBtn.apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
        bottomBar.addView(aiAutoBtn.apply { layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
        root.addView(bottomBar)
    }

    private fun createBottomButton(text: String): Button = Button(context).apply {
        this.text = text
        setTextColor(Color.WHITE)
        setBackgroundColor(0x4DFFFFFF.toInt())
        textSize = 11f
        setPadding(12, 8, 12, 8)
    }

    private fun createCaptureButton(): Button = Button(context).apply {
        text = "●"
        setTextColor(Color.WHITE)
        setBackgroundColor(0xFF2196F3.toInt())
        textSize = 32f
        layoutParams = FrameLayout.LayoutParams(80, 80).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
    }

    private fun createSliderRow(label: String, range: String): LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 60)
        setPadding(16, 8, 16, 8)

        addView(TextView(context).apply {
            text = label
            setTextColor(Color.WHITE)
            textSize = 12f
        })
        addView(SeekBar(context).apply {
            layoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        })
        addView(TextView(context).apply {
            text = range
            setTextColor(0x99FFFFFF.toInt())
            textSize = 10f
        })
    }

    fun showProControls(show: Boolean) {
        proControlsPanel.visibility = if (show) VISIBLE else GONE
    }

    fun updateGridMode(mode: GridMode) {
        gridOverlay.setMode(mode)
    }

    fun updateHistogram(luma: Int) {
        histogramOverlay.pushLuma(luma)
    }

    fun updateLevel(roll: Float) {
        levelOverlay.setRoll(roll)
    }

    fun showFocusPoint(x: Float, y: Float) {
        focusIndicator.showPoint(x, y)
    }

    enum class GridMode { NONE, RULE_OF_THIRDS, GRID_4X4, DIAGONALS }
}
