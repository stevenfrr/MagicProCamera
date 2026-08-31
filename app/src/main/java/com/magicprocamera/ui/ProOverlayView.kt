package com.magicprocamera.ui
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.math.max
import kotlin.math.min

/** Lightweight composition grid and local luminance histogram. */
class ProOverlayView(context: Context):View(context) {
 var gridVisible=true
 private val bins=IntArray(32)
 private val line=Paint(Paint.ANTI_ALIAS_FLAG).apply{color=0x99FFFFFF.toInt();strokeWidth=1.5f}
 private val histogram=Paint(Paint.ANTI_ALIAS_FLAG).apply{color=0xCC70D6FF.toInt();strokeWidth=3f}
 fun pushLuma(luma:Int){for(i in bins.indices)bins[i]=max(0,bins[i]-3);val index=(luma.coerceIn(0,255)*bins.size/256).coerceIn(0,bins.lastIndex);bins[index]=min(100,bins[index]+22);invalidate()}
 override fun onDraw(c:Canvas){super.onDraw(c);if(gridVisible){c.drawLine(width/3f,0f,width/3f,height.toFloat(),line);c.drawLine(width*2/3f,0f,width*2/3f,height.toFloat(),line);c.drawLine(0f,height/3f,width.toFloat(),height/3f,line);c.drawLine(0f,height*2/3f,width.toFloat(),height*2/3f,line)}
  val left=24f;val bottom=height-220f;val barWidth=(width-48f)/bins.size;for(i in bins.indices){val h=bins[i]*1.2f;c.drawLine(left+i*barWidth,bottom,left+i*barWidth,bottom-h,histogram)}
 }
}