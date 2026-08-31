package com.magicprocamera.ai.enhancement
import android.graphics.*
interface PhotoEnhancer{fun enhance(source:Bitmap):Bitmap}
/** Local baseline; a future on-device denoising model can replace it. */
class LocalColorEnhancer:PhotoEnhancer{override fun enhance(source:Bitmap):Bitmap{val out=Bitmap.createBitmap(source.width,source.height,source.config?:Bitmap.Config.ARGB_8888);val matrix=ColorMatrix().apply{setSaturation(1.08f)};val paint=Paint(Paint.ANTI_ALIAS_FLAG).apply{colorFilter=ColorMatrixColorFilter(matrix)};Canvas(out).drawBitmap(source,0f,0f,paint);return out}}