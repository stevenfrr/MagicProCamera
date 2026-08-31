package com.magicprocamera.device

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Range
import android.util.Size

data class Lens(
    val focalLength: Float,
    val aperture: Float,
    val isWide: Boolean = false,
    val isTelephoto: Boolean = false
)

data class DeviceCapabilities(
    val cameraId: String,
    val facing: Int,
    val hasManualSensor: Boolean,
    val hasRaw: Boolean,
    val hasManualFocus: Boolean,
    val hasAutoFocus: Boolean,
    val isoRange: Range<Int>?,
    val shutterRange: Range<Long>?,
    val evRange: Range<Int>?,
    val lenses: List<Lens>,
    val maxZoom: Float,
    val hasOpticalZoom: Boolean,
    val photoSizes: List<Size>,
    val videoSizes: List<Size>,
    val hasFlash: Boolean,
    val hasHdr: Boolean,
    val hasStabilization: Boolean,
    val hasAfLock: Boolean,
    val hasAwbLock: Boolean,
    val maxAnalogSensitivity: Int,
    val hysteresisMode: Boolean,
    val exposureCompensationStep: Float
) {
    fun isBudgetDevice() = !hasManualSensor && !hasRaw
    fun isProDevice() = hasManualSensor && hasRaw && isoRange != null && shutterRange != null
}

object DeviceCapabilityReader {
    fun back(context: Context): DeviceCapabilities? = read(context, CameraCharacteristics.LENS_FACING_BACK)
    fun front(context: Context): DeviceCapabilities? = read(context, CameraCharacteristics.LENS_FACING_FRONT)

    private fun read(context: Context, facing: Int): DeviceCapabilities? {
        val manager = context.getSystemService(CameraManager::class.java) ?: return null
        val cameraId = manager.cameraIdList.firstOrNull {
            manager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING) == facing
        } ?: return null

        val chars = manager.getCameraCharacteristics(cameraId)
        val caps = chars.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)?.toSet().orEmpty()
        val streamMap = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

        // Photo sizes
        val photoSizes = streamMap?.getOutputSizes(ImageFormat.JPEG)?.toList().orEmpty()
            .distinctBy { it.width to it.height }
            .sortedByDescending { it.width.toLong() * it.height }

        // Video sizes
        val videoSizes = streamMap?.getOutputSizes(ImageFormat.PRIVATE)?.toList().orEmpty()
            .distinctBy { it.width to it.height }
            .sortedByDescending { it.width.toLong() * it.height }

        // Lenses
        val lenses = (chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS) ?: floatArrayOf())
            .zip(chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES) ?: floatArrayOf())
            .mapIndexed { idx, (focal, aperture) ->
                Lens(
                    focalLength = focal,
                    aperture = aperture,
                    isWide = focal < 35f,
                    isTelephoto = focal > 70f && idx > 0
                )
            }

        val hasManualSensor = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR in caps
        val hasRaw = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW in caps
        val isoRange = chars.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)
        val shutterRange = chars.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)
        val evRange = chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)
        val maxZoom = chars.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) ?: 1f
        val hasOpticalZoom = lenses.size > 1
        val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        val autoFocusModes = chars.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)?.toSet().orEmpty()
        val hasAutoFocus = CameraCharacteristics.CONTROL_AF_MODE_AUTO in autoFocusModes
        val hasManualFocus = hasManualSensor
        val afLockModes = chars.get(CameraCharacteristics.CONTROL_AF_LOCK_AVAILABLE) == true
        val awbLockModes = chars.get(CameraCharacteristics.CONTROL_AWB_LOCK_AVAILABLE) == true
        val maxAnalogSensitivity = chars.get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY) ?: 100
        val hysteresisMode = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING in caps
        val evStep = chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP) ?: (1.0f / 3.0f)

        // Check HDR and stabilization from capabilities
        val hasHdr = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE in caps
        val opticalStabilization = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)
            ?: booleanArrayOf(false)
        val hasStabilization = opticalStabilization.any { it }

        return DeviceCapabilities(
            cameraId = cameraId,
            facing = facing,
            hasManualSensor = hasManualSensor,
            hasRaw = hasRaw,
            hasManualFocus = hasManualFocus,
            hasAutoFocus = hasAutoFocus,
            isoRange = isoRange,
            shutterRange = shutterRange,
            evRange = evRange,
            lenses = lenses,
            maxZoom = maxZoom,
            hasOpticalZoom = hasOpticalZoom,
            photoSizes = photoSizes,
            videoSizes = videoSizes,
            hasFlash = hasFlash,
            hasHdr = hasHdr,
            hasStabilization = hasStabilization,
            hasAfLock = afLockModes,
            hasAwbLock = awbLockModes,
            maxAnalogSensitivity = maxAnalogSensitivity,
            hysteresisMode = hysteresisMode,
            exposureCompensationStep = evStep
        )
    }
}
