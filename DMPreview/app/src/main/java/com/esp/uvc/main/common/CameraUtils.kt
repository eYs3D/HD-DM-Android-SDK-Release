package com.esp.uvc.main.common

import com.esp.android.usb.camera.core.ApcCamera
import com.esp.android.usb.camera.core.ApcCamera.*
import java.text.SimpleDateFormat
import java.util.*

object CameraUtils {
    fun isIRSupported(productVersion: String): Boolean {
        return when (productVersion) {
            PRODUCT_VERSION_EX8029 -> true
            PRODUCT_VERSION_EX8036 -> true
            PRODUCT_VERSION_EX8037 -> true
            PRODUCT_VERSION_EX8038 -> true
            PRODUCT_VERSION_EX8059 -> true
            PRODUCT_VERSION_YX8059 -> true
            else -> false
        }
    }

    fun getIRMAXValue(apcCamera: ApcCamera): Int {
        var value: Int
        value = apcCamera.irMaxValue
        if (value == -1 && apcCamera.productVersion == PRODUCT_VERSION_EX8029) {
            value = 0x10
        }
        return value
    }

    fun getCurrentIRValue(apcCamera: ApcCamera): Int {
        return apcCamera.irCurrentValue
    }

    fun getDateTimeString(): String {
        val mDateTimeFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US)
        val now = GregorianCalendar()
        return mDateTimeFormat.format(now.time)
    }


    fun getZDTableValue(apcCamera: ApcCamera, depthHeight: Int, color: Int): IntArray {
        val mProductVersion = apcCamera.productVersion
        if (mProductVersion == PRODUCT_VERSION_EX8036) {
            if (!apcCamera.isUSB3 && color != 0 && depthHeight != 0 && (color % depthHeight != 0)) {
                // For mode 34 35 on PIF
                return apcCamera.getZDTableValue(2)
            }
            if (depthHeight == 720) {
                return apcCamera.zdTableValue
            } else if (depthHeight >= 480) {
                return apcCamera.getZDTableValue(1)
            }
            return apcCamera.zdTableValue
        }
        if (mProductVersion == PRODUCT_VERSION_EX8037) {
            return when {
                depthHeight >= 720 -> {
                    apcCamera.zdTableValue
                }
                depthHeight >= 480 -> {
                    apcCamera.getZDTableValue(1)
                }
                else -> {
                    apcCamera.zdTableValue
                }
            }
        } else {
            return if (!apcCamera.isUSB3 || depthHeight >= 720) {
                apcCamera.zdTableValue
            } else {
                apcCamera.getZDTableValue(1)
            }
        }
    }

    fun getMinIRValue(apcCamera: ApcCamera): Int {
        return apcCamera.irMinValue
    }


}
