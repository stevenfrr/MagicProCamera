package com.magicprocamera.ai.scenerecognition
import androidx.camera.core.ImageProxy
import kotlin.math.abs
import kotlin.math.roundToInt
enum class Scene{BRIGHT,INTERIOR,LOW_LIGHT,NIGHT,MOTION}
data class SceneResult(val scene:Scene,val confidence:Int,val luma:Int,val motion:Boolean){val label get()=when(scene){Scene.BRIGHT->"Lumière forte";Scene.INTERIOR->"Intérieur";Scene.LOW_LIGHT->"Faible luminosité";Scene.NIGHT->"Nuit";Scene.MOTION->"Mouvement / action"}}
/** Local and throttled; camera frames never leave the device. */
class AiSceneAnalyzer(private val callback:(SceneResult)->Unit){private var previous=-1;private var last=0L
 fun analyze(image:ImageProxy){try{val now=System.currentTimeMillis();if(now-last<450)return;last=now;val b=image.planes[0].buffer;val d=ByteArray(minOf(4096,b.remaining()));b.get(d);val luma=d.map{it.toInt() and 255}.average().roundToInt();val moving=previous>=0&&abs(luma-previous)>18;previous=luma;val s=when{moving->Scene.MOTION;luma<24->Scene.NIGHT;luma<65->Scene.LOW_LIGHT;luma>175->Scene.BRIGHT;else->Scene.INTERIOR};callback(SceneResult(s,when(s){Scene.NIGHT->92;Scene.BRIGHT->82;Scene.LOW_LIGHT->75;Scene.MOTION->72;else->68},luma,moving))}finally{image.close()}}
}