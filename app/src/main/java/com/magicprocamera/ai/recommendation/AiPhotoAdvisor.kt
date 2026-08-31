package com.magicprocamera.ai.recommendation

import com.magicprocamera.ai.scenerecognition.Scene
import com.magicprocamera.ai.scenerecognition.SceneResult
import com.magicprocamera.camera.ManualSettings
import com.magicprocamera.device.DeviceCapabilities

data class PhotoRecommendation(
    val title: String,
    val settings: ManualSettings,
    val blurRisk: String,
    val note: String,
    val emoji: String = "📷"
)

object AiPhotoAdvisor {
    fun recommend(scene: SceneResult, caps: DeviceCapabilities?): PhotoRecommendation {
        val recommendation = when (scene.scene) {
            Scene.BRIGHT -> PhotoRecommendation(
                title = "Lumière forte",
                settings = ManualSettings(iso = 50, shutterNs = 2_000_000, ev = -1, autoFocus = true),
                blurRisk = "Faible",
                note = "Ferme bien l'ouverture, utilise ND filter si disponible",
                emoji = "☀️"
            )
            Scene.INTERIOR -> PhotoRecommendation(
                title = "Intérieur",
                settings = ManualSettings(iso = 400, shutterNs = 10_000_000, ev = 0, autoFocus = true),
                blurRisk = "Moyen",
                note = "Rapproche-toi de la lumière, stabilise bien",
                emoji = "🏠"
            )
            Scene.LOW_LIGHT -> PhotoRecommendation(
                title = "Basse lumière",
                settings = ManualSettings(iso = 800, shutterNs = 33_000_000, ev = 1, autoFocus = true),
                blurRisk = "Élevé",
                note = "Utilise un trépied, mode IA AUTO recommandé",
                emoji = "🌅"
            )
            Scene.NIGHT -> PhotoRecommendation(
                title = "Nuit",
                settings = ManualSettings(iso = 1600, shutterNs = 125_000_000, ev = 0, autoFocus = false),
                blurRisk = "Très élevé",
                note = "Trépied obligatoire, focus manuel sur lampadaire/lune",
                emoji = "🌙"
            )
            Scene.MOTION -> PhotoRecommendation(
                title = "Mouvement détecté",
                settings = ManualSettings(iso = 400, shutterNs = 4_000_000, ev = 0, autoFocus = true),
                blurRisk = "Élevé",
                note = "Augmente la vitesse pour figer le mouvement",
                emoji = "🏃"
            )
            Scene.SUNSET -> PhotoRecommendation(
                title = "Coucher de soleil",
                settings = ManualSettings(iso = 100, shutterNs = 8_000_000, ev = 0, kelvin = 3500, autoFocus = true),
                blurRisk = "Faible",
                note = "Augmente la saturation en post-prod",
                emoji = "🌅"
            )
            Scene.PORTRAIT -> PhotoRecommendation(
                title = "Portrait",
                settings = ManualSettings(iso = 100, shutterNs = 15_000_000, ev = 0, autoFocus = true),
                blurRisk = "Très faible",
                note = "Utilise le bokeh, éloigne du sujet",
                emoji = "👤"
            )
            Scene.MACRO -> PhotoRecommendation(
                title = "Macro",
                settings = ManualSettings(iso = 200, shutterNs = 8_000_000, ev = 0, autoFocus = true),
                blurRisk = "Très élevé",
                note = "Rapproche extrêmement, stabilise parfaitement",
                emoji = "🔬"
            )
        }

        return recommendation.copy(
            settings = recommendation.settings.clamp(caps)
        )
    }
}

object AiPhotoAssistant {
    fun advise(query: String): PhotoRecommendation {
        val q = query.lowercase()
        return when {
            "lune" in q || "étoile" in q || "ciel étoilé" in q -> PhotoRecommendation(
                title = "Ciel étoilé",
                settings = ManualSettings(iso = 800, shutterNs = 125_000_000, ev = 0, autoFocus = false),
                blurRisk = "Très élevé",
                note = "Trépied + déclencheur à distance",
                emoji = "⭐"
            )
            "coucher" in q || "lever" in q || "golden hour" in q -> PhotoRecommendation(
                title = "Coucher/Lever de soleil",
                settings = ManualSettings(iso = 100, shutterNs = 8_000_000, ev = 0, kelvin = 3500),
                blurRisk = "Faible",
                note = "Profite de la lumière dorée",
                emoji = "🌅"
            )
            "chien" in q || "chat" in q || "animal" in q && "cours" in q || "court" in q -> PhotoRecommendation(
                title = "Animal en mouvement",
                settings = ManualSettings(iso = 400, shutterNs = 3_000_000, ev = 0, autoFocus = true),
                blurRisk = "Élevé",
                note = "AF continu, préfocus sur zone de passage",
                emoji = "🐕"
            )
            "nuit" in q || "soir" in q -> PhotoRecommendation(
                title = "Photographie de nuit",
                settings = ManualSettings(iso = 1600, shutterNs = 125_000_000, ev = 0, autoFocus = false),
                blurRisk = "Très élevé",
                note = "Trépied + Focus manuel sur lampadaire/lune",
                emoji = "🌙"
            )
            "paysage" in q || "montagne" in q || "mer" in q -> PhotoRecommendation(
                title = "Paysage",
                settings = ManualSettings(iso = 50, shutterNs = 4_000_000, ev = 0, autoFocus = true),
                blurRisk = "Faible",
                note = "Utilise la profondeur de champ, f/8 idéal",
                emoji = "🏔️"
            )
            "portrait" in q || "visage" in q -> PhotoRecommendation(
                title = "Portrait",
                settings = ManualSettings(iso = 100, shutterNs = 15_000_000, ev = 0, autoFocus = true),
                blurRisk = "Très faible",
                note = "Bokeh arrière-plan, lumière frontale douce",
                emoji = "👤"
            )
            "nourriture" in q || "food" in q -> PhotoRecommendation(
                title = "Photographie culinaire",
                settings = ManualSettings(iso = 200, shutterNs = 10_000_000, ev = 0, autoFocus = true),
                blurRisk = "Moyen",
                note = "Lumière naturelle latérale, légère surexposition",
                emoji = "🍔"
            )
            else -> PhotoRecommendation(
                title = "Mode AUTO",
                settings = ManualSettings(iso = null, shutterNs = null, autoFocus = true),
                blurRisk = "Variable",
                note = "Laisse l'IA gérer automatiquement",
                emoji = "🤖"
            )
        }
    }
}
