package com.magicprocamera

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.magicprocamera.ai.recommendation.*
import com.magicprocamera.ai.scenerecognition.*
import com.magicprocamera.camera.*
import com.magicprocamera.device.DeviceCapabilityReader
import com.magicprocamera.modes.PhotoModeController
import com.magicprocamera.modes.ProModeController
import com.magicprocamera.presets.SmartPresetSelector
import com.magicprocamera.settings.CameraSettingsStore
import com.magicprocamera.ui.ModernCameraUI

class MainActivity : AppCompatActivity() {
    private lateinit var ui: ModernCameraUI
    private lateinit var controller: CameraController
    private lateinit var store: CameraSettingsStore
    private var currentScene: SceneResult? = null
    private var currentCaps = DeviceCapabilityReader.back(this)
    private var isProMode = false
    private var aiAutoEnabled = true

    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) startCamera() else showError("Permission caméra nécessaire")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = CameraSettingsStore(this)
        aiAutoEnabled = store.aiAuto()
        currentCaps = DeviceCapabilityReader.back(this)

        // Initialize modern UI
        ui = ModernCameraUI(this)
        setContentView(ui)

        // Initialize camera controller with scene analyzer
        val analyzer = AiSceneAnalyzer { scene ->
            updateUI(scene)
        }
        val preview = PreviewView(this)
        controller = CameraController(this, this, preview, analyzer)

        setupUIListeners()
        checkCameraPermission()
    }

    private fun setupUIListeners() {
        // Mode switching
        ui.photoModeBtn.setOnClickListener {
            switchToPhotoMode()
        }
        ui.proModeBtn.setOnClickListener {
            switchToProMode()
        }
        ui.aiAutoBtn.setOnClickListener {
            aiAutoEnabled = !aiAutoEnabled
            store.saveAiAuto(aiAutoEnabled)
            ui.aiAutoBtn.alpha = if (aiAutoEnabled) 1f else 0.5f
            updateUIFromAI()
        }

        // Camera controls
        ui.captureBtn.setOnClickListener {
            takePhoto()
        }
        ui.switchCameraBtn.setOnClickListener {
            switchCamera()
        }
        ui.galleryBtn.setOnClickListener {
            openGallery()
        }
        ui.settingsBtn.setOnClickListener {
            showSettings()
        }

        // Grid toggle (long press)
        ui.gridOverlay.setOnLongClickListener {
            cycleGridMode()
            true
        }

        // Focus point (tap)
        ui.centerOverlay.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                ui.showFocusPoint(event.x, event.y)
            }
            true
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            permission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        currentCaps = DeviceCapabilityReader.back(this)
        val capsInfo = if (currentCaps?.isProDevice() == true) {
            "Mode Pro disponible"
        } else if (currentCaps?.isBudgetDevice() == true) {
            "Mode Budget - IA activée pour compensation"
        } else {
            "Mode Hybride"
        }
        updateStatus("📷 Caméra prête • $capsInfo")
        controller.start { error -> showError(error) }
    }

    private fun updateUI(scene: SceneResult) {
        currentScene = scene
        ui.sceneLabel.text = "${scene.label} (${scene.confidence}%)"
        ui.histogramOverlay.pushLuma(scene.luma)

        if (aiAutoEnabled) {
            updateUIFromAI()
        }
    }

    private fun updateUIFromAI() {
        currentScene?.let { scene ->
            val recommendation = AiPhotoAdvisor.recommend(scene, currentCaps)
            val preset = SmartPresetSelector.choosePreset(scene, currentCaps)

            // Update indicators
            ui.flashIndicator.alpha = if (currentCaps?.hasFlash == true) 1f else 0.3f
            ui.hdrIndicator.alpha = if (currentCaps?.hasHdr == true) 1f else 0.3f
            ui.stabilizationIndicator.alpha = if (currentCaps?.hasStabilization == true) 1f else 0.3f

            // Show recommendation
            showStatus(
                "${recommendation.emoji} ${recommendation.title}\n" +
                        "📸 ${preset.emoji} ${preset.label}\n" +
                        "⚠️ ${recommendation.blurRisk}"
            )
        }
    }

    private fun switchToPhotoMode() {
        isProMode = false
        ui.photoModeBtn.alpha = 1f
        ui.proModeBtn.alpha = 0.5f
        ui.showProControls(false)
        updateStatus("📷 Mode PHOTO - IA Automatique")
    }

    private fun switchToProMode() {
        if (currentCaps?.isProDevice() != true) {
            showError("Mode PRO non supporté sur cet appareil")
            return
        }
        isProMode = true
        ui.photoModeBtn.alpha = 0.5f
        ui.proModeBtn.alpha = 1f
        ui.showProControls(true)
        updateStatus("⚙️ Mode PRO - Contrôle Manuel")
    }

    private fun cycleGridMode() {
        val modes = ModernCameraUI.GridMode.values()
        val current = (modes.indexOf(ui.gridOverlay.mode) + 1) % modes.size
        ui.updateGridMode(modes[current])
    }

    private fun takePhoto() {
        controller.take(
            { path ->
                showStatus("✅ Photo sauvegardée: $path")
            },
            { error -> showError(error) }
        )
    }

    private fun switchCamera() {
        showStatus("Caméra frontale - À implémenter")
    }

    private fun openGallery() {
        showStatus("Galerie - À implémenter")
    }

    private fun showSettings() {
        val options = arrayOf(
            "Qualité photo",
            "Ratio d'aspect",
            "Format RAW (si dispo)",
            "À propos"
        )
        AlertDialog.Builder(this)
            .setTitle("⚙️ Paramètres")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showPhotoQualityDialog()
                    1 -> showAspectRatioDialog()
                    2 -> showRawFormatDialog()
                    3 -> showAbout()
                }
            }
            .show()
    }

    private fun showPhotoQualityDialog() {
        val sizes = currentCaps?.photoSizes.orEmpty()
        if (sizes.isEmpty()) {
            showError("Aucune taille photo disponible")
            return
        }
        val labels = sizes.map { "${it.width}×${it.height}" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Qualité photo")
            .setItems(labels) { _, which ->
                controller.selectPhotoSize(sizes[which]) { error -> showError(error) }
            }
            .show()
    }

    private fun showAspectRatioDialog() {
        val ratios = PhotoModeController.getAvailableRatios(currentCaps)
        val labels = ratios.map { it.label }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Ratio d'aspect")
            .setItems(labels) { _, which ->
                updateStatus("Ratio: ${labels[which]}")
            }
            .show()
    }

    private fun showRawFormatDialog() {
        if (currentCaps?.hasRaw != true) {
            showError("Format RAW non supporté")
            return
        }
        showStatus("Format RAW disponible - Capture en cours...")
    }

    private fun showAbout() {
        AlertDialog.Builder(this)
            .setTitle("À propos de MagicProCamera")
            .setMessage(
                "🎥 MagicProCamera v2.0\n\n" +
                        "📱 Détection d'appareil: ${currentCaps?.let { if (it.isProDevice()) "PRO" else "UNIVERSAL" } ?: "Inconnu"}\n" +
                        "🤖 IA locale: Activée\n" +
                        "📸 Présets: 15 modes\n" +
                        "🎨 UI: Moderne & Intuitive\n\n" +
                        "© 2026 MagicProCamera"
            )
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, "❌ $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showStatus(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStatus(message: String) {
        runOnUiThread {
            ui.sceneLabel.text = message
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.close()
    }
}
