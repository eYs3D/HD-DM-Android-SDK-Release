package com.esp.uvc.manager

import com.esp.android.usb.camera.core.ApcCamera
import com.esp.android.usb.camera.core.ApcCamera.*

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

    fun getAE(apcCamera: ApcCamera?): Int {
        return apcCamera?.exposureMode ?: EYS_ERROR
    }

    fun getAEBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.AUTO_EXPOSURE, EYS_ERROR) as Int
    }

    fun isAE(apcCamera: ApcCamera?): Boolean {
        return getAE(apcCamera) == EXPOSURE_MODE_AUTO_APERTURE
    }

    fun setAE(apcCamera: ApcCamera?, enabled: Boolean): Int {
        val value = if (enabled) EXPOSURE_MODE_AUTO_APERTURE else EXPOSURE_MODE_MANUAL
        return apcCamera?.setExposureMode(value) ?: EYS_ERROR
    }

    fun setAEBySharedPrefs(apcCamera: ApcCamera?) {
        val ae = getAEBySharedPrefs()
        if (ae >= 0) {
            if (ae != getAE(apcCamera)) {
                setAE(apcCamera, ae == EXPOSURE_MODE_AUTO_APERTURE)
            }
            if (ae == EXPOSURE_MODE_MANUAL) {
                setExposureAbsoluteTimeBySharedPrefs(apcCamera)
            }
        }
    }

    /**
     * Exposure
     **/

    fun getExposureAbsoluteTime(apcCamera: ApcCamera?): Int {
        return apcCamera?.exposureAbsoluteTime ?: DEVICE_FIND_FAIL
    }

    fun getExposureAbsoluteTimeBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.EXPOSURE_ABSOLUTE_TIME, DEVICE_FIND_FAIL) as Int
    }

    fun setExposureAbsoluteTime(apcCamera: ApcCamera?, current: Int): Int {
        return apcCamera?.setExposureAbsoluteTime(current) ?: EYS_ERROR
    }

    fun setExposureAbsoluteTimeBySharedPrefs(apcCamera: ApcCamera?, force: Boolean = false) {
        val time =
            getExposureAbsoluteTimeBySharedPrefs()
        if (force || (time != DEVICE_FIND_FAIL && time != DEVICE_NOT_SUPPORT && time != getExposureAbsoluteTime(
                apcCamera
            ))
        ) {
            setExposureAbsoluteTime(apcCamera, time)
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

    fun setupSharedPrefs(apcCamera: ApcCamera?) {
        SharedPrefManager.put(KEY.AUTO_EXPOSURE, getAE(apcCamera))
        // Avoid after setting EXPOSURE_ABSOLUTE_TIME by SensorSettingsActivity and the ae is on, then go preview and back to SensorSettingsActivity
        if (getExposureAbsoluteTimeBySharedPrefs() == DEVICE_FIND_FAIL) {
            SharedPrefManager.put(KEY.EXPOSURE_ABSOLUTE_TIME, getExposureAbsoluteTime(apcCamera))
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
