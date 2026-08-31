package com.magicprocamera.camera
import android.content.*
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.magicprocamera.ai.scenerecognition.AiSceneAnalyzer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
class CameraController(private val context:Context,private val owner:LifecycleOwner,private val view:PreviewView,private val analyzer:AiSceneAnalyzer) {
 private val executor=Executors.newSingleThreadExecutor();private var capture:ImageCapture?=null
 fun start(error:(String)->Unit){val future=ProcessCameraProvider.getInstance(context);future.addListener({
  try {val provider=future.get();val preview=Preview.Builder().build().also{it.setSurfaceProvider(view.surfaceProvider)}
   capture=ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
   val analysis=ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also{it.setAnalyzer(executor){frame->analyzer.analyze(frame)}}
   provider.unbindAll();provider.bindToLifecycle(owner,CameraSelector.DEFAULT_BACK_CAMERA,preview,capture,analysis)
  }catch(e:Exception){error(e.message?:"Caméra indisponible")}
 },ContextCompat.getMainExecutor(context))}
 fun take(saved:(String)->Unit,error:(String)->Unit){val cap=capture?:return error("Caméra non prête");val values=ContentValues().apply{put(MediaStore.MediaColumns.DISPLAY_NAME,"MPC_"+SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(Date()));put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+"/MagicProCamera")};val output=ImageCapture.OutputFileOptions.Builder(context.contentResolver,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values).build();cap.takePicture(output,ContextCompat.getMainExecutor(context),object:ImageCapture.OnImageSavedCallback{override fun onImageSaved(r:ImageCapture.OutputFileResults){saved(r.savedUri?.toString()?:"JPEG enregistré")}override fun onError(e:ImageCaptureException){error(e.message?:"Capture impossible")}})}
 fun close(){executor.shutdown()}
}