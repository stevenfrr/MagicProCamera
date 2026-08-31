package com.magicprocamera
import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.magicprocamera.ai.recommendation.*
import com.magicprocamera.ai.scenerecognition.*
import com.magicprocamera.camera.*
import com.magicprocamera.settings.CameraSettingsStore
class MainActivity:AppCompatActivity(){
 private lateinit var controller:CameraController;private lateinit var scene:TextView;private lateinit var advice:TextView;private lateinit var status:TextView;private var caps:CameraCapabilities?=null;private var aiAuto=false;private lateinit var store:CameraSettingsStore
 private val permission=registerForActivityResult(ActivityResultContracts.RequestPermission()){if(it)startCamera()else status.text="Autorisation caméra nécessaire."}
 override fun onCreate(b:Bundle?){super.onCreate(b);store=CameraSettingsStore(this);aiAuto=store.aiAuto();ui();if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)startCamera()else permission.launch(Manifest.permission.CAMERA)}
 private fun ui(){val root=FrameLayout(this).apply{setBackgroundColor(Color.BLACK)};val preview=PreviewView(this).apply{scaleType=PreviewView.ScaleType.FILL_CENTER};root.addView(preview);val panel=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(28,24,28,24);setBackgroundColor(0xB0080B10.toInt())};scene=label("🤖 Analyse locale en attente");advice=label("Réglages IA à venir");status=label("Mode Pro • JPEG");panel.addView(scene);panel.addView(advice);panel.addView(status);val actions=LinearLayout(this).apply{gravity=Gravity.CENTER};actions.addView(button(if(aiAuto)"IA AUTO ✓" else "✨ IA AUTO"){aiAuto=!aiAuto;store.saveAiAuto(aiAuto);it.text=if(aiAuto)"IA AUTO ✓" else "✨ IA AUTO";status.text=if(aiAuto)"IA AUTO active" else "Mode Pro manuel"});actions.addView(button("Assistant IA"){assistant()});panel.addView(actions);panel.addView(button("● CAPTURER"){controller.take({status.text="JPEG enregistré : "+it},{status.text=it})});root.addView(panel,FrameLayout.LayoutParams(-1,ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.BOTTOM));setContentView(root);controller=CameraController(this,this,preview,AiSceneAnalyzer{runOnUiThread{update(it)}})}
 private fun label(t:String)=TextView(this).apply{text=t;setTextColor(Color.WHITE);textSize=15f}
 private fun button(t:String,f:(Button)->Unit)=Button(this).apply{text=t;setOnClickListener{f(this)}}
 private fun startCamera(){caps=CameraCapabilityReader.back(this);status.text="Mode Pro • JPEG • ISO/vitesse: "+if(caps?.manual==true)"disponibles" else "non supportés";controller.start{status.text=it}}
 private fun update(r:SceneResult){val a=AiPhotoAdvisor.recommend(r,caps);scene.text="🤖 "+r.label+" — "+r.confidence+" %";advice.text="📷 "+a.title+"  ISO "+(a.settings.iso?:"Auto")+" • "+((a.settings.shutterNs?:0)/1_000_000)+" ms • "+(a.settings.kelvin?:"Auto")+" K • flou: "+a.blurRisk;if(aiAuto)status.text="✓ IA AUTO — conseil local prêt à appliquer"}
 private fun assistant(){val input=EditText(this).apply{hint="Ex. Je veux photographier mon chien qui court"};AlertDialog.Builder(this).setTitle("Assistant photo IA local").setView(input).setPositiveButton("Conseiller"){_,_->val a=AiPhotoAssistant.advise(input.text.toString());advice.text="✨ "+a.title+" : ISO "+a.settings.iso+" • "+((a.settings.shutterNs?:0)/1_000_000)+" ms • "+a.settings.kelvin+" K\n"+a.note}.setNegativeButton("Annuler",null).show()}
 override fun onDestroy(){super.onDestroy();controller.close()}
}