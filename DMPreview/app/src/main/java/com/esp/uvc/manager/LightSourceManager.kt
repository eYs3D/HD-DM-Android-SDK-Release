package com.esp.uvc.manager

import com.esp.android.usb.camera.core.EtronCamera
import com.esp.android.usb.camera.core.EtronCamera.*
import com.esp.uvc.application.AndroidApplication

private const val DEFAULT_CURRENT_LIGHT_SOURCE = 2
private const val DEFAULT_LOW_LIGHT_COMPENSATION = 0

object LightSourceManager {

    const val INDEX_CURRENT_LIGHT_SOURCE = 0
    const val INDEX_LOW_LIGHT_COMPENSATION = 1

    /**
     * Light Source
     **/

    fun getCurrentLS(etronCamera: EtronCamera?): Int {
        return etronCamera?.currentPowerlineFrequency ?: EYS_ERROR
    }

    fun getCurrentLSBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.CURRENT_LIGHT_SOURCE, EYS_ERROR) as Int
    }

    fun setCurrentLS(etronCamera: EtronCamera?, value: Int): Int {
        return etronCamera?.setCurrentPowerlineFrequency(value) ?: EYS_ERROR
    }

    fun setCurrentLSBySharedPrefs(etronCamera: EtronCamera?) {
        val currentLS = getCurrentLSBySharedPrefs()
        if (currentLS >= 0 && currentLS != getCurrentLS(etronCamera)) {
            setCurrentLS(etronCamera, currentLS)
        }
    }

    fun getLSLimit(etronCamera: EtronCamera?): IntArray? {
        return etronCamera?.powerlineFrequencyLimit
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

    fun getLLC(etronCamera: EtronCamera?): Int {
        return etronCamera?.exposurePriority ?: EYS_ERROR
    }

    fun getLLCBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.LOW_LIGHT_COMPENSATION, EYS_ERROR) as Int
    }

    fun isLLC(etronCamera: EtronCamera?): Boolean {
        return getLLC(etronCamera) == LOW_LIGHT_COMPENSATION_ON
    }

    fun setLLC(etronCamera: EtronCamera?, enabled: Boolean): Int {
        val value = if (enabled) LOW_LIGHT_COMPENSATION_ON else LOW_LIGHT_COMPENSATION_OFF
        return etronCamera?.setExposurePriority(value) ?: EYS_ERROR
    }

    fun setLLCBySharedPrefs(etronCamera: EtronCamera?) {
        val value = getLLCBySharedPrefs()
        if (value >= 0 && value != getLLC(etronCamera)) {
            setLLC(etronCamera, value == LOW_LIGHT_COMPENSATION_ON)
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

    fun setupSharedPrefs(etronCamera: EtronCamera?) {
        val currentLS = getCurrentLS(etronCamera)
        SharedPrefManager.put(KEY.CURRENT_LIGHT_SOURCE, currentLS)
        val limitLS = getLSLimit(etronCamera)
        SharedPrefManager.put(KEY.MIN_LIGHT_SOURCE, limitLS?.get(0) ?: EYS_ERROR)
        SharedPrefManager.put(KEY.MAX_LIGHT_SOURCE, limitLS?.get(1) ?: EYS_ERROR)
        val llc = getLLC(etronCamera)
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