package com.esp.uvc.manager

import com.esp.android.usb.camera.core.EtronCamera
import com.esp.android.usb.camera.core.EtronCamera.*
import com.esp.uvc.application.AndroidApplication

private const val DEFAULT_AUTO_WHITE_BALANCE = 1
private const val DEFAULT_CURRENT_WHITE_BALANCE = 5500

object WhiteBalanceManager {

    const val INDEX_AUTO_WHITE_BALANCE = 0
    const val INDEX_CURRENT_WHITE_BALANCE = 1

    /**
     * Auto White Balance
     **/

    fun getAWB(etronCamera: EtronCamera?): Int {
        return etronCamera?.autoWhiteBalance ?: EYS_ERROR
    }

    fun getAWBBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.AUTO_WHITE_BALANCE, EYS_ERROR) as Int
    }

    fun isAWB(etronCamera: EtronCamera?): Boolean {
        return getAWB(etronCamera) == AUTO_WHITE_BALANCE_ON
    }

    fun setAWB(etronCamera: EtronCamera?, enabled: Boolean): Int {
        return etronCamera?.setAutoWhiteBalance(enabled) ?: EYS_ERROR
    }

    fun setAWBBySharedPrefs(etronCamera: EtronCamera?) {
        val awb = getAWBBySharedPrefs()
        if (awb >= 0) {
            if (awb != getAWB(etronCamera)) {
                setAWB(etronCamera, awb == AUTO_WHITE_BALANCE_ON)
            }
            if (awb == AUTO_WHITE_BALANCE_OFF) {
                setCurrentWBBySharedPrefs(etronCamera)
            }
        }
    }

    /**
     * White Balance
     **/

    fun getCurrentWB(etronCamera: EtronCamera?): Int {
        return etronCamera?.currentWhiteBalance ?: EYS_ERROR
    }

    fun getCurrentWBBySharedPrefs(): Int {
        return SharedPrefManager.get(KEY.CURRENT_WHITE_BALANCE, EYS_ERROR) as Int
    }

    fun setCurrentWB(etronCamera: EtronCamera?, current: Int): Int {
        return etronCamera?.setCurrentWhiteBalance(current) ?: EYS_ERROR
    }

    fun setCurrentWBBySharedPrefs(etronCamera: EtronCamera?, force: Boolean = false) {
        val current = getCurrentWBBySharedPrefs()
        if (force || (current > 0 && current != getCurrentWB(etronCamera))) {
            setCurrentWB(etronCamera, current)
        }
    }

    fun getWBLimit(etronCamera: EtronCamera?): IntArray? {
        return etronCamera?.whiteBalanceLimit
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

    fun setupSharedPrefs(etronCamera: EtronCamera?) {
        SharedPrefManager.put(KEY.AUTO_WHITE_BALANCE, getAWB(etronCamera))
        // Avoid after setting CURRENT_WHITE_BALANCE by SensorSettingsActivity and the awb is on, then go preview and back to SensorSettingsActivity
        if (getCurrentWBBySharedPrefs() < 0) {
            SharedPrefManager.put(KEY.CURRENT_WHITE_BALANCE, getCurrentWB(etronCamera))
        }
        val arrayMinMaxWB = getWBLimit(etronCamera)
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