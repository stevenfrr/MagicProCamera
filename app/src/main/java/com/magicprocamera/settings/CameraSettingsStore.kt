package com.magicprocamera.settings
import android.content.Context
class CameraSettingsStore(context:Context){private val prefs=context.getSharedPreferences("magic_pro_camera",Context.MODE_PRIVATE);fun saveAiAuto(enabled:Boolean){prefs.edit().putBoolean("ai_auto",enabled).apply()};fun aiAuto()=prefs.getBoolean("ai_auto",false)}