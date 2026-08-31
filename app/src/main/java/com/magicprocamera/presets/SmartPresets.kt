package com.magicprocamera.presets
import com.magicprocamera.ai.recommendation.PhotoRecommendation
import com.magicprocamera.ai.scenerecognition.SceneResult
enum class SmartPreset(val label:String){FULL_SUN("Plein soleil"),CLOUDY("Nuageux"),SUNRISE("Lever de soleil"),GOLDEN_HOUR("Golden Hour"),BLUE_HOUR("Heure bleue"),TWILIGHT("Crépuscule"),NIGHT("Nuit"),STARRY_SKY("Ciel étoilé"),INDOOR("Intérieur"),ARTIFICIAL_LIGHT("Lumière artificielle"),PORTRAIT("Portrait"),ACTION("Action")}
object SmartPresetSelector{fun choose(scene:SceneResult,recommendation:PhotoRecommendation):SmartPreset=when{scene.motion->SmartPreset.ACTION;recommendation.title=="Nuit"->SmartPreset.NIGHT;recommendation.title=="Lumière forte"->SmartPreset.FULL_SUN;else->SmartPreset.INDOOR}}