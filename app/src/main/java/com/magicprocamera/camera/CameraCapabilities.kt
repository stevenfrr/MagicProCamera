package com.magicprocamera.camera
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Range
data class CameraCapabilities(val cameraId:String,val manual:Boolean,val raw:Boolean,val manualFocus:Boolean,val iso:Range<Int>?,val shutter:Range<Long>?,val ev:Range<Int>?,val lenses:List<String>)
object CameraCapabilityReader {
 fun back(context:Context):CameraCapabilities? {
  val manager=context.getSystemService(CameraManager::class.java)
  val id=manager.cameraIdList.firstOrNull { manager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING)==CameraCharacteristics.LENS_FACING_BACK } ?: return null
  val c=manager.getCameraCharacteristics(id);val caps=c.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)?.toSet().orEmpty()
  return CameraCapabilities(id,CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR in caps,CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW in caps,(c.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)?:0f)>0f,c.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE),c.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE),c.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE),manager.cameraIdList.toList())
 }
}
data class ManualSettings(val iso:Int?=null,val shutterNs:Long?=null,val ev:Int=0,val kelvin:Int?=null,val autoFocus:Boolean=true) {
 fun clamp(c:CameraCapabilities?):ManualSettings { if(c==null)return this;return copy(iso=iso?.let{c.iso?.let{r->it.coerceIn(r.lower,r.upper)}},shutterNs=shutterNs?.let{c.shutter?.let{r->it.coerceIn(r.lower,r.upper)}},ev=c.ev?.let{ev.coerceIn(it.lower,it.upper)}?:0) }
}