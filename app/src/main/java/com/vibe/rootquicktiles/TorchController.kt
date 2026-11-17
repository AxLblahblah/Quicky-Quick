package com.vibe.rootquicktiles

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper

object TorchController {
    @Volatile
    var on: Boolean = false
        private set

    private var manager: CameraManager? = null
    private var cameraId: String? = null

    fun init(context: Context) {
        manager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        cameraId = manager?.cameraIdList?.firstOrNull { id ->
            runCatching {
                val characteristics = manager!!.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                    characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            }.getOrDefault(false)
        }
        manager?.registerTorchCallback(object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(id: String, enabled: Boolean) {
                if (id == cameraId) on = enabled
            }
        }, Handler(Looper.getMainLooper()))
    }

    fun toggle() {
        val id = cameraId ?: return
        runCatching { manager?.setTorchMode(id, !on) }
    }
}
