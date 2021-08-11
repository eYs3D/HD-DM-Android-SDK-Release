package com.esp.uvc.manager

import com.esp.android.usb.camera.core.ApcCamera
import com.esp.android.usb.camera.core.ApcCamera.*
import com.esp.uvc.application.AndroidApplication

private const val DEFAULT_AUTO_WHITE_BALANCE = 1
private const val DEFAULT_CURRENT_WHITE_BALANCE = 5500

object WhiteBalanceManager {

    const val INDEX_AUTO_WHITE_BALANCE = 0
    const val INDEX_CURRENT_WHITE_BALANCE = 1

    /**
     * Auto White Balance
     **/

    fun getAWB(apcCamera: ApcCamera?): Int {
        return apcCamera?.autoWhiteBalance ?: EYS_ERROR
    }

    fun getAWBBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.AUTO_WHITE_BALANCE, EYS_ERROR) as Int
    }

    fun isAWB(apcCamera: ApcCamera?): Boolean {
        return getAWB(apcCamera) == AUTO_WHITE_BALANCE_ON
    }

    fun setAWB(apcCamera: ApcCamera?, enabled: Boolean): Int {
        return apcCamera?.setAutoWhiteBalance(enabled) ?: EYS_ERROR
    }

    fun setAWBBySharedPrefs(apcCamera: ApcCamera?) {
        val awb = getAWBBySharedPrefs()
        if (awb >= 0) {
            if (awb != getAWB(apcCamera)) {
                setAWB(apcCamera, awb == AUTO_WHITE_BALANCE_ON)
            }
            if (awb == AUTO_WHITE_BALANCE_OFF) {
                setCurrentWBBySharedPrefs(apcCamera)
            }
        }
    }

    /**
     * White Balance
     **/

    fun getCurrentWB(apcCamera: ApcCamera?): Int {
        return apcCamera?.currentWhiteBalance ?: EYS_ERROR
    }

    fun getCurrentWBBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.CURRENT_WHITE_BALANCE, EYS_ERROR) as Int
    }

    fun setCurrentWB(apcCamera: ApcCamera?, current: Int): Int {
        return apcCamera?.setCurrentWhiteBalance(current) ?: EYS_ERROR
    }

    fun setCurrentWBBySharedPrefs(apcCamera: ApcCamera?, force: Boolean = false) {
        val current = getCurrentWBBySharedPrefs()
        if (force || (current > 0 && current != getCurrentWB(apcCamera))) {
            setCurrentWB(apcCamera, current)
        }
    }

    fun getWBLimit(apcCamera: ApcCamera?): IntArray? {
        return apcCamera?.whiteBalanceLimit
    }

    fun getWBLimitBySharedPrefs(): IntArray {
        val intArray = IntArray(2)
        intArray[0] = SharedPrefManager.get(KEY.MIN_WHITE_BALANCE, EYS_ERROR) as Int
        intArray[1] = SharedPrefManager.get(KEY.MAX_WHITE_BALANCE, EYS_ERROR) as Int
        return intArray
    }

    /**
     * Common
     **/

    fun setSharedPrefs(index: Int, value: Any) {
        when (index) {
            INDEX_AUTO_WHITE_BALANCE -> SharedPrefManager.put(KEY.AUTO_WHITE_BALANCE, value)
            INDEX_CURRENT_WHITE_BALANCE -> SharedPrefManager.put(KEY.CURRENT_WHITE_BALANCE, value)
        }
        SharedPrefManager.saveAll()
    }

    fun setupSharedPrefs(apcCamera: ApcCamera?) {
        SharedPrefManager.put(KEY.AUTO_WHITE_BALANCE, getAWB(apcCamera))
        // Avoid after setting CURRENT_WHITE_BALANCE by SensorSettingsActivity and the awb is on, then go preview and back to SensorSettingsActivity
        if (getCurrentWBBySharedPrefs() < 0) {
            SharedPrefManager.put(KEY.CURRENT_WHITE_BALANCE, getCurrentWB(apcCamera))
        }
        val arrayMinMaxWB = getWBLimit(apcCamera)
        SharedPrefManager.put(KEY.MIN_WHITE_BALANCE, arrayMinMaxWB?.get(0) ?: EYS_ERROR)
        SharedPrefManager.put(KEY.MAX_WHITE_BALANCE, arrayMinMaxWB?.get(1) ?: EYS_ERROR)
        SharedPrefManager.saveAll()
    }

    fun defaultSharedPrefs(): IntArray {
        var awb = getAWBBySharedPrefs()
        if (awb >= 0) {
            SharedPrefManager.put(KEY.AUTO_WHITE_BALANCE, DEFAULT_AUTO_WHITE_BALANCE)
            awb = DEFAULT_AUTO_WHITE_BALANCE
        }
        var wb = getCurrentWBBySharedPrefs()
        if (wb > 0) {
            SharedPrefManager.put(KEY.CURRENT_WHITE_BALANCE, DEFAULT_CURRENT_WHITE_BALANCE)
            wb = DEFAULT_CURRENT_WHITE_BALANCE
        }
        SharedPrefManager.saveAll()
        val result = IntArray(2)
        result[INDEX_AUTO_WHITE_BALANCE] = awb
        result[INDEX_CURRENT_WHITE_BALANCE] = wb
        return result
    }
}
