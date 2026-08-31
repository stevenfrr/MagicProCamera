package com.magicprocamera.modes

import com.magicprocamera.camera.ManualSettings
import com.magicprocamera.device.DeviceCapabilities

data class PhotoMode(
    val name: String = "PHOTO",
    val isAutomatic: Boolean = true
)

data class PhotoModeSettings(
    val ratio: AspectRatio = AspectRatio.RATIO_4_3,
    val flash: FlashMode = FlashMode.AUTO,
    val timerSeconds: Int = 0,
    val hdrEnabled: Boolean = false,
    val qualityIndex: Int = 0
)

enum class AspectRatio(val label: String, val width: Int, val height: Int) {
    RATIO_4_3("4:3", 4, 3),
    RATIO_16_9("16:9", 16, 9),
    RATIO_1_1("1:1", 1, 1),
    RATIO_3_2("3:2", 3, 2),
    RATIO_9_16("9:16", 9, 16)
}

enum class FlashMode(val label: String, val emoji: String) {
    OFF("Off", "🌙"),
    ON("On", "⚡"),
    AUTO("Auto", "🔦"),
    REDEYE("Red-eye", "👁️")
}

object PhotoModeController {
    fun getAvailableRatios(caps: DeviceCapabilities?): List<AspectRatio> {
        if (caps == null) return listOf(AspectRatio.RATIO_4_3)
        val availableRatios = mutableListOf(AspectRatio.RATIO_4_3, AspectRatio.RATIO_16_9, AspectRatio.RATIO_1_1)
        if (caps.lenses.isNotEmpty()) availableRatios.add(AspectRatio.RATIO_3_2)
        return availableRatios
    }

    fun getAvailableFlashModes(caps: DeviceCapabilities?): List<FlashMode> {
        if (caps?.hasFlash == true) {
            return listOf(FlashMode.OFF, FlashMode.AUTO, FlashMode.ON, FlashMode.REDEYE)
        }
        return listOf(FlashMode.OFF)
    }

    fun getRecommendedSettings(caps: DeviceCapabilities?): ManualSettings {
        val iso = caps?.isoRange?.let { (it.lower + it.upper) / 2 } ?: 100
        val shutter = caps?.shutterRange?.let { (it.lower + it.upper) / 2 } ?: 30_000_000L
        return ManualSettings(iso = iso, shutterNs = shutter, autoFocus = true)
    }
}
