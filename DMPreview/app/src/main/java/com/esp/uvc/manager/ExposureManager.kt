package com.esp.uvc.manager

import com.esp.android.usb.camera.core.EtronCamera
import com.esp.android.usb.camera.core.EtronCamera.*

private const val MAX_EXPOSURE_ABSOLUTE_TIME = 3
private const val MIN_EXPOSURE_ABSOLUTE_TIME = -13

private const val DEFAULT_AUTO_EXPOSURE = EXPOSURE_MODE_AUTO_APERTURE
private const val DEFAULT_EXPOSURE_ABSOLUTE_TIME = -13

object ExposureManager {

    const val INDEX_AUTO_EXPOSURE = 0
    const val INDEX_EXPOSURE_ABSOLUTE_TIME = 1

    /**
     * Auto Exposure
     **/

    fun getAE(etronCamera: EtronCamera?): Int {
        return etronCamera?.exposureMode ?: EYS_ERROR
    }

    fun getAEBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.AUTO_EXPOSURE, EYS_ERROR) as Int
    }

    fun isAE(etronCamera: EtronCamera?): Boolean {
        return getAE(etronCamera) == EXPOSURE_MODE_AUTO_APERTURE
    }

    fun setAE(etronCamera: EtronCamera?, enabled: Boolean): Int {
        val value = if (enabled) EXPOSURE_MODE_AUTO_APERTURE else EXPOSURE_MODE_MANUAL
        return etronCamera?.setExposureMode(value) ?: EYS_ERROR
    }

    fun setAEBySharedPrefs(etronCamera: EtronCamera?) {
        val ae = getAEBySharedPrefs()
        if (ae >= 0) {
            if (ae != getAE(etronCamera)) {
                setAE(etronCamera, ae == EXPOSURE_MODE_AUTO_APERTURE)
            }
            if (ae == EXPOSURE_MODE_MANUAL) {
                setExposureAbsoluteTimeBySharedPrefs(etronCamera)
            }
        }
    }

    /**
     * Exposure
     **/

    fun getExposureAbsoluteTime(etronCamera: EtronCamera?): Int {
        return etronCamera?.exposureAbsoluteTime ?: DEVICE_FIND_FAIL
    }

    fun getExposureAbsoluteTimeBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.EXPOSURE_ABSOLUTE_TIME, DEVICE_FIND_FAIL) as Int
    }

    fun setExposureAbsoluteTime(etronCamera: EtronCamera?, current: Int): Int {
        return etronCamera?.setExposureAbsoluteTime(current) ?: EYS_ERROR
    }

    fun setExposureAbsoluteTimeBySharedPrefs(etronCamera: EtronCamera?, force: Boolean = false) {
        val time =
            getExposureAbsoluteTimeBySharedPrefs()
        if (force || (time != DEVICE_FIND_FAIL && time != DEVICE_NOT_SUPPORT && time != getExposureAbsoluteTime(
                etronCamera
            ))
        ) {
            setExposureAbsoluteTime(etronCamera, time)
        }
    }

    fun getExposureAbsoluteTimeLimit(): IntArray {
        val intArray = IntArray(2)
        intArray[0] = MIN_EXPOSURE_ABSOLUTE_TIME
        intArray[1] = MAX_EXPOSURE_ABSOLUTE_TIME
        return intArray
    }

    /**
     * Common
     **/

    fun setSharedPrefs(index: Int, value: Any) {
        when (index) {
            INDEX_AUTO_EXPOSURE -> SharedPrefManager.put(KEY.AUTO_EXPOSURE, value)
            INDEX_EXPOSURE_ABSOLUTE_TIME -> SharedPrefManager.put(KEY.EXPOSURE_ABSOLUTE_TIME, value)
        }
        SharedPrefManager.saveAll()
    }

    fun setupSharedPrefs(etronCamera: EtronCamera?) {
        SharedPrefManager.put(KEY.AUTO_EXPOSURE, getAE(etronCamera))
        // Avoid after setting EXPOSURE_ABSOLUTE_TIME by SensorSettingsActivity and the ae is on, then go preview and back to SensorSettingsActivity
        if (getExposureAbsoluteTimeBySharedPrefs() == DEVICE_FIND_FAIL) {
            SharedPrefManager.put(KEY.EXPOSURE_ABSOLUTE_TIME, getExposureAbsoluteTime(etronCamera))
        }
        SharedPrefManager.saveAll()
    }

    fun defaultSharedPrefs(): IntArray {
        var ae = getAEBySharedPrefs()
        if (ae >= 0) {
            SharedPrefManager.put(KEY.AUTO_EXPOSURE, DEFAULT_AUTO_EXPOSURE)
            ae = DEFAULT_AUTO_EXPOSURE
        }
        var time = getExposureAbsoluteTimeBySharedPrefs()
        if (time != DEVICE_FIND_FAIL && time != DEVICE_NOT_SUPPORT) {
            SharedPrefManager.put(KEY.EXPOSURE_ABSOLUTE_TIME, DEFAULT_EXPOSURE_ABSOLUTE_TIME)
            time = DEFAULT_EXPOSURE_ABSOLUTE_TIME
        }
        SharedPrefManager.saveAll()
        val result = IntArray(2)
        result[INDEX_AUTO_EXPOSURE] = ae
        result[INDEX_EXPOSURE_ABSOLUTE_TIME] = time
        return result
    }
}