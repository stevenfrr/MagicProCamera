package com.magicprocamera.ai.scenerecognition

import androidx.camera.core.ImageProxy
import kotlin.math.abs
import kotlin.math.roundToInt

enum class Scene {
    BRIGHT, INTERIOR, LOW_LIGHT, NIGHT, MOTION, SUNSET, PORTRAIT, MACRO
}

data class SceneResult(
    val scene: Scene,
    val confidence: Int,
    val luma: Int,
    val motion: Boolean,
    val contrast: Int = 0,
    val saturation: Int = 0
) {
    val label: String
        get() = when (scene) {
            Scene.BRIGHT -> "☀️ Lumière forte"
            Scene.INTERIOR -> "🏠 Intérieur"
            Scene.LOW_LIGHT -> "🌅 Basse lumière"
            Scene.NIGHT -> "🌙 Nuit"
            Scene.MOTION -> "🏃 Mouvement"
            Scene.SUNSET -> "🌅 Coucher soleil"
            Scene.PORTRAIT -> "👤 Portrait"
            Scene.MACRO -> "🔬 Macro"
        }
}

/** Local and throttled; camera frames never leave the device. */
class AiSceneAnalyzer(private val callback: (SceneResult) -> Unit) {
    private var previous = -1
    private var last = 0L
    private var motionBuffer = IntArray(5)
    private var bufferIdx = 0

    fun analyze(image: ImageProxy) {
        try {
            val now = System.currentTimeMillis()
            if (now - last < 450) return
            last = now

            val plane = image.planes[0]
            val buffer = plane.buffer
            val pixelStride = plane.pixelStride
            val data = ByteArray(minOf(8192, buffer.remaining()))
            buffer.get(data)

            // Calculate luminance
            val luma = data.map { it.toInt() and 0xFF }.average().roundToInt().coerceIn(0, 255)

            // Calculate contrast
            val avg = luma
            val contrast = data.map { abs((it.toInt() and 0xFF) - avg) }.average().roundToInt()

            // Calculate saturation (simplified)
            val saturation = contrast / 2

            // Motion detection via luminance variance
            motionBuffer[bufferIdx] = luma
            bufferIdx = (bufferIdx + 1) % motionBuffer.size
            val motionVariance = motionBuffer.maxOrNull()?.minus(motionBuffer.minOrNull() ?: 0) ?: 0
            val hasMotion = motionVariance > 30

            // Scene classification
            val scene = when {
                luma > 200 && contrast < 40 -> Scene.BRIGHT
                luma in 100..150 -> Scene.INTERIOR
                luma in 40..100 -> Scene.LOW_LIGHT
                luma < 40 -> Scene.NIGHT
                hasMotion -> Scene.MOTION
                saturation > 80 && luma > 120 -> Scene.SUNSET
                contrast > 100 -> Scene.MACRO
                else -> Scene.PORTRAIT
            }

            val confidence = (80 + (20 * abs(luma - 127) / 127)).coerceIn(0, 100)
            val result = SceneResult(
                scene = scene,
                confidence = confidence,
                luma = luma,
                motion = hasMotion,
                contrast = contrast,
                saturation = saturation
            )

            if (result.luma != previous) {
                previous = result.luma
                callback(result)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            image.close()
        }
    }
}
