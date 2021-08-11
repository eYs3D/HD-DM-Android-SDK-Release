package com.esp.uvc.manager

import com.esp.android.usb.camera.core.ApcCamera
import com.esp.android.usb.camera.core.ApcCamera.*
import com.esp.uvc.application.AndroidApplication

private const val DEFAULT_CURRENT_LIGHT_SOURCE = 2
private const val DEFAULT_LOW_LIGHT_COMPENSATION = 0

object LightSourceManager {

    const val INDEX_CURRENT_LIGHT_SOURCE = 0
    const val INDEX_LOW_LIGHT_COMPENSATION = 1

    /**
     * Light Source
     **/

    fun getCurrentLS(apcCamera: ApcCamera?): Int {
        return apcCamera?.currentPowerlineFrequency ?: EYS_ERROR
    }

    fun getCurrentLSBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.CURRENT_LIGHT_SOURCE, EYS_ERROR) as Int
    }

    fun setCurrentLS(apcCamera: ApcCamera?, value: Int): Int {
        return apcCamera?.setCurrentPowerlineFrequency(value) ?: EYS_ERROR
    }

    fun setCurrentLSBySharedPrefs(apcCamera: ApcCamera?) {
        val currentLS = getCurrentLSBySharedPrefs()
        if (currentLS >= 0 && currentLS != getCurrentLS(apcCamera)) {
            setCurrentLS(apcCamera, currentLS)
        }
    }

    fun getLSLimit(apcCamera: ApcCamera?): IntArray? {
        return apcCamera?.powerlineFrequencyLimit
    }

    fun getLSLimitBySharedPrefs(): IntArray {
        val intArray = IntArray(2)
        intArray[0] = SharedPrefManager.get(KEY.MIN_LIGHT_SOURCE, EYS_ERROR) as Int
        intArray[1] = SharedPrefManager.get(KEY.MAX_LIGHT_SOURCE, EYS_ERROR) as Int
        return intArray
    }

    /**
     * Low Light Compensation
     **/

    fun getLLC(apcCamera: ApcCamera?): Int {
        return apcCamera?.exposurePriority ?: EYS_ERROR
    }

    fun getLLCBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.LOW_LIGHT_COMPENSATION, EYS_ERROR) as Int
    }

    fun isLLC(apcCamera: ApcCamera?): Boolean {
        return getLLC(apcCamera) == LOW_LIGHT_COMPENSATION_ON
    }

    fun setLLC(apcCamera: ApcCamera?, enabled: Boolean): Int {
        val value = if (enabled) LOW_LIGHT_COMPENSATION_ON else LOW_LIGHT_COMPENSATION_OFF
        return apcCamera?.setExposurePriority(value) ?: EYS_ERROR
    }

    fun setLLCBySharedPrefs(apcCamera: ApcCamera?) {
        val value = getLLCBySharedPrefs()
        if (value >= 0 && value != getLLC(apcCamera)) {
            setLLC(apcCamera, value == LOW_LIGHT_COMPENSATION_ON)
        }
    }

    /**
     * Common
     **/

    fun setSharedPrefs(index: Int, value: Any) {
        when (index) {
            INDEX_CURRENT_LIGHT_SOURCE -> SharedPrefManager.put(KEY.CURRENT_LIGHT_SOURCE, value)
            INDEX_LOW_LIGHT_COMPENSATION -> SharedPrefManager.put(KEY.LOW_LIGHT_COMPENSATION, value)
        }
        SharedPrefManager.saveAll()
    }

    fun setupSharedPrefs(apcCamera: ApcCamera?) {
        val currentLS = getCurrentLS(apcCamera)
        SharedPrefManager.put(KEY.CURRENT_LIGHT_SOURCE, currentLS)
        val limitLS = getLSLimit(apcCamera)
        SharedPrefManager.put(KEY.MIN_LIGHT_SOURCE, limitLS?.get(0) ?: EYS_ERROR)
        SharedPrefManager.put(KEY.MAX_LIGHT_SOURCE, limitLS?.get(1) ?: EYS_ERROR)
        val llc = getLLC(apcCamera)
        SharedPrefManager.put(KEY.LOW_LIGHT_COMPENSATION, llc)
        SharedPrefManager.saveAll()
    }

    fun defaultSharedPrefs(): IntArray {
        var ls = getCurrentLSBySharedPrefs()
        if (ls >= 0) {
            SharedPrefManager.put(KEY.CURRENT_LIGHT_SOURCE, DEFAULT_CURRENT_LIGHT_SOURCE)
            ls = DEFAULT_CURRENT_LIGHT_SOURCE
        }
        var llc = getLLCBySharedPrefs()
        if (llc >= 0) {
            SharedPrefManager.put(KEY.LOW_LIGHT_COMPENSATION, DEFAULT_LOW_LIGHT_COMPENSATION)
            llc = DEFAULT_LOW_LIGHT_COMPENSATION
        }
        SharedPrefManager.saveAll()
        val result = IntArray(2)
        result[INDEX_CURRENT_LIGHT_SOURCE] = ls
        result[INDEX_LOW_LIGHT_COMPENSATION] = llc
        return result
    }
}
