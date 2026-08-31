package com.magicprocamera.modes

import com.magicprocamera.camera.ManualSettings
import com.magicprocamera.device.DeviceCapabilities
import kotlin.math.log10

data class ProMode(
    val name: String = "PRO",
    val isManual: Boolean = true
)

data class ProModeSettings(
    val iso: Int = 100,
    val isoAuto: Boolean = false,
    val shutterNs: Long = 30_000_000L,
    val shutterAuto: Boolean = true,
    val ev: Int = 0,
    val kelvin: Int = 5500,
    val whiteBalanceAuto: Boolean = true,
    val focusDistance: Float = 0f,
    val focusAuto: Boolean = true,
    val lensIndex: Int = 0,
    val zoom: Float = 1f,
    val rawCapture: Boolean = false
) {
    fun toManualSettings(): ManualSettings {
        return ManualSettings(
            iso = if (isoAuto) null else iso,
            shutterNs = if (shutterAuto) null else shutterNs,
            ev = ev,
            kelvin = if (whiteBalanceAuto) null else kelvin,
            autoFocus = focusAuto
        )
    }
}

object ProModeController {
    fun clampSettings(settings: ProModeSettings, caps: DeviceCapabilities?): ProModeSettings {
        if (caps == null) return settings

        val clamped = settings.copy(
            iso = settings.iso.coerceIn(
                caps.isoRange?.lower ?: 50,
                caps.isoRange?.upper ?: 3200
            ),
            shutterNs = settings.shutterNs.coerceIn(
                caps.shutterRange?.lower ?: 1_000_000L,
                caps.shutterRange?.upper ?: 1_000_000_000L
            ),
            ev = settings.ev.coerceIn(
                caps.evRange?.lower ?: -10,
                caps.evRange?.upper ?: 10
            ),
            kelvin = settings.kelvin.coerceIn(2000, 10000),
            zoom = settings.zoom.coerceIn(1f, caps.maxZoom)
        )

        return clamped
    }

    fun getISORange(caps: DeviceCapabilities?): IntRange {
        val lower = caps?.isoRange?.lower ?: 50
        val upper = caps?.isoRange?.upper ?: 3200
        return lower..upper
    }

    fun getShutterRange(caps: DeviceCapabilities?): LongRange {
        val lower = caps?.shutterRange?.lower ?: 1_000_000L
        val upper = caps?.shutterRange?.upper ?: 1_000_000_000L
        return lower..upper
    }

    fun getEVRange(caps: DeviceCapabilities?): IntRange {
        val lower = caps?.evRange?.lower ?: -10
        val upper = caps?.evRange?.upper ?: 10
        return lower..upper
    }

    fun formatShutterTime(ns: Long): String {
        return when {
            ns < 1_000_000L -> String.format("1/%.0f", 1_000_000_000f / ns)
            ns < 1_000_000_000L -> String.format("%.2f ms", ns / 1_000_000f)
            else -> String.format("%.2f s", ns / 1_000_000_000f)
        }
    }

    fun formatISO(iso: Int): String = iso.toString()

    fun formatEV(ev: Int, step: Float = 1f/3f): String = String.format("%.1f", ev * step)

    fun formatKelvin(k: Int): String = "${k}K"
}
