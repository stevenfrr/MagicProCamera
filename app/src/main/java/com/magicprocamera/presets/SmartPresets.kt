package com.magicprocamera.presets

import com.magicprocamera.ai.scenerecognition.SceneResult
import com.magicprocamera.camera.ManualSettings
import com.magicprocamera.device.DeviceCapabilities

enum class SmartPreset(
    val label: String,
    val emoji: String,
    val description: String
) {
    FULL_SUN("☀️ Plein soleil", "☀️", "Lumière très forte, ISO bas"),
    CLOUDY("☁️ Nuageux", "☁️", "Lumière diffuse, température neutre"),
    SUNRISE("🌅 Lever soleil", "🌅", "Teintes chaudes, lumière faible"),
    GOLDEN_HOUR("🌇 Golden Hour", "🌇", "Lumière dévérifiée, teintes dorées"),
    BLUE_HOUR("🔵 Heure bleue", "🔵", "Crépuscule, tons bleutés"),
    TWILIGHT("🌆 Crépuscule", "🌆", "Transition jour-nuit"),
    NIGHT("🌙 Nuit", "🌙", "Basse lumière, ISO élevé"),
    INTERIOR("🏠 Intérieur", "🏠", "Lumière intérieure, WB chaud"),
    PORTRAIT("👤 Portrait", "👤", "Bokeh, focus sujet"),
    ANIMAL("🐕 Animal", "🐕", "Mouvement, vitesse rapide"),
    ACTION("🏃 Action", "🏃", "Mouvement rapide, vitesse élevée"),
    FOOD("🍔 Nourriture", "🍔", "Macro, lumière douce"),
    NATURE("🌿 Nature", "🌿", "Saturation élevée, contraste"),
    ARCHITECTURE("🏛️ Architecture", "🏛️", "Lignes, perspective"),
    MACRO("🔬 Macro", "🔬", "Très rapproché, focus manuel"),
    AI_SMART("✨ IA AUTO", "✨", "Sélection automatique par IA")
}

data class PresetSettings(
    val iso: Int,
    val shutterNs: Long,
    val ev: Int,
    val kelvin: Int,
    val autoFocus: Boolean
) {
    fun toManualSettings(): ManualSettings {
        return ManualSettings(
            iso = iso,
            shutterNs = shutterNs,
            ev = ev,
            kelvin = kelvin,
            autoFocus = autoFocus
        )
    }
}

object SmartPresetSelector {
    fun getPresetSettings(preset: SmartPreset, caps: DeviceCapabilities?): PresetSettings {
        val settings = when (preset) {
            SmartPreset.FULL_SUN -> PresetSettings(50, 2_000_000L, -1, 5500, true)
            SmartPreset.CLOUDY -> PresetSettings(100, 4_000_000L, 0, 6500, true)
            SmartPreset.SUNRISE -> PresetSettings(100, 8_000_000L, 0, 3500, true)
            SmartPreset.GOLDEN_HOUR -> PresetSettings(100, 10_000_000L, 0, 3800, true)
            SmartPreset.BLUE_HOUR -> PresetSettings(200, 30_000_000L, 0, 6500, true)
            SmartPreset.TWILIGHT -> PresetSettings(400, 50_000_000L, 1, 5500, true)
            SmartPreset.NIGHT -> PresetSettings(1600, 125_000_000L, 0, 5500, false)
            SmartPreset.INTERIOR -> PresetSettings(400, 15_000_000L, 1, 4500, true)
            SmartPreset.PORTRAIT -> PresetSettings(100, 15_000_000L, 0, 5500, true)
            SmartPreset.ANIMAL -> PresetSettings(400, 4_000_000L, 0, 5500, true)
            SmartPreset.ACTION -> PresetSettings(800, 2_000_000L, 0, 5500, true)
            SmartPreset.FOOD -> PresetSettings(200, 10_000_000L, 0, 4500, true)
            SmartPreset.NATURE -> PresetSettings(100, 8_000_000L, 0, 5500, true)
            SmartPreset.ARCHITECTURE -> PresetSettings(100, 8_000_000L, 0, 5500, true)
            SmartPreset.MACRO -> PresetSettings(200, 8_000_000L, 0, 5500, true)
            SmartPreset.AI_SMART -> PresetSettings(100, 30_000_000L, 0, 5500, true)
        }

        // Clamp to device capabilities
        return settings.copy(
            iso = settings.iso.coerceIn(
                caps?.isoRange?.lower ?: 50,
                caps?.isoRange?.upper ?: 3200
            ),
            shutterNs = settings.shutterNs.coerceIn(
                caps?.shutterRange?.lower ?: 1_000_000L,
                caps?.shutterRange?.upper ?: 1_000_000_000L
            ),
            ev = settings.ev.coerceIn(
                caps?.evRange?.lower ?: -10,
                caps?.evRange?.upper ?: 10
            )
        )
    }

    fun choosePreset(scene: SceneResult, caps: DeviceCapabilities?): SmartPreset {
        return when {
            scene.motion -> SmartPreset.ACTION
            scene.luma < 40 -> SmartPreset.NIGHT
            scene.luma in 40..100 -> SmartPreset.TWILIGHT
            scene.luma in 100..150 -> SmartPreset.INTERIOR
            scene.luma > 200 -> SmartPreset.FULL_SUN
            scene.contrast > 100 -> SmartPreset.MACRO
            scene.saturation > 80 && scene.luma > 120 -> SmartPreset.GOLDEN_HOUR
            else -> SmartPreset.CLOUDY
        }
    }

    fun getAvailablePresets(caps: DeviceCapabilities?): List<SmartPreset> {
        val presets = mutableListOf(
            SmartPreset.FULL_SUN,
            SmartPreset.CLOUDY,
            SmartPreset.SUNRISE,
            SmartPreset.GOLDEN_HOUR,
            SmartPreset.TWILIGHT,
            SmartPreset.NIGHT,
            SmartPreset.INTERIOR,
            SmartPreset.PORTRAIT,
            SmartPreset.ANIMAL,
            SmartPreset.ACTION,
            SmartPreset.FOOD,
            SmartPreset.NATURE,
            SmartPreset.ARCHITECTURE,
            SmartPreset.MACRO,
            SmartPreset.AI_SMART
        )
        return presets
    }
}
