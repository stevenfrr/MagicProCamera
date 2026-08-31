-keepattributes *Annotation*
-keep class com.magicprocamera.** { *; }
-keep class androidx.camera.** { *; }
-keep class android.hardware.camera2.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
