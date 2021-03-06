package com.esp.uvc.main.settings.sensor

import com.esp.android.usb.camera.core.ApcCamera.*
import com.esp.uvc.manager.*
import com.esp.uvc.manager.ExposureManager.INDEX_AUTO_EXPOSURE
import com.esp.uvc.manager.ExposureManager.INDEX_EXPOSURE_ABSOLUTE_TIME
import com.esp.uvc.manager.LightSourceManager.INDEX_CURRENT_LIGHT_SOURCE
import com.esp.uvc.manager.LightSourceManager.INDEX_LOW_LIGHT_COMPENSATION
import com.esp.uvc.manager.WhiteBalanceManager.INDEX_AUTO_WHITE_BALANCE
import com.esp.uvc.manager.WhiteBalanceManager.INDEX_CURRENT_WHITE_BALANCE

private const val INTERLEAVE_MODE_LLC = -1010

class MSensorSettings(p: ISensor.Presenter) : ISensor.Model {

    private val mIPresenter = p

    /**
     * Exposure
     */

    override fun isAutoExposure() {
        when (val result = ExposureManager.getAEBySharedPrefs()) {
            EXPOSURE_MODE_MANUAL, EXPOSURE_MODE_AUTO_APERTURE -> mIPresenter.onAutoExposure(enabled = result == EXPOSURE_MODE_AUTO_APERTURE)
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedAutoExposure()
            else -> mIPresenter.onAutoExposure(errorMsg(result), false)
        }
    }

    override fun setAutoExposure(enabled: Boolean) {
        when (val result = ExposureManager.getAEBySharedPrefs()) {
            EXPOSURE_MODE_MANUAL, EXPOSURE_MODE_AUTO_APERTURE -> {
                val value = if (enabled) EXPOSURE_MODE_AUTO_APERTURE else EXPOSURE_MODE_MANUAL
                ExposureManager.setSharedPrefs(INDEX_AUTO_EXPOSURE, value)
                mIPresenter.onAutoExposure(enabled = enabled)
            }
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedAutoExposure()
            else -> mIPresenter.onAutoExposure(errorMsg(result), false)
        }
    }

    override fun getExposureTime() {
        when (val result = ExposureManager.getExposureAbsoluteTimeBySharedPrefs()) {
            DEVICE_FIND_FAIL -> mIPresenter.onExposureTime(errorMsg(result), DEVICE_FIND_FAIL)
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedExposureTime()
            else -> mIPresenter.onExposureTime(value = result)
        }
    }

    override fun getExposureTimeLimit() {
        val limit = ExposureManager.getExposureAbsoluteTimeLimit()
        mIPresenter.onExposureTimeLimit(limit[0], limit[1])
    }

    override fun setExposureTime(value: Int) {
        when (val result = ExposureManager.getExposureAbsoluteTimeBySharedPrefs()) {
            DEVICE_FIND_FAIL -> mIPresenter.onExposureTime(errorMsg(result), DEVICE_FIND_FAIL)
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedExposureTime()
            else -> ExposureManager.setSharedPrefs(INDEX_EXPOSURE_ABSOLUTE_TIME, value)
        }
    }

    /**
     * White balance
     */

    override fun isAutoWhiteBalance() {
        when (val result = WhiteBalanceManager.getAWBBySharedPrefs()) {
            AUTO_WHITE_BALANCE_OFF, AUTO_WHITE_BALANCE_ON -> mIPresenter.onAutoWhiteBalance(enabled = result == AUTO_WHITE_BALANCE_ON)
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedAutoWhiteBalance()
            else -> mIPresenter.onAutoWhiteBalance(errorMsg(result), false)
        }
    }

    override fun setAutoWhiteBalance(enabled: Boolean) {
        when (val result = WhiteBalanceManager.getAWBBySharedPrefs()) {
            AUTO_WHITE_BALANCE_OFF, AUTO_WHITE_BALANCE_ON -> {
                val value = if (enabled) AUTO_WHITE_BALANCE_ON else AUTO_WHITE_BALANCE_OFF
                WhiteBalanceManager.setSharedPrefs(INDEX_AUTO_WHITE_BALANCE, value)
                mIPresenter.onAutoWhiteBalance(enabled = enabled)
            }
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedAutoWhiteBalance()
            else -> mIPresenter.onAutoWhiteBalance(errorMsg(result), false)
        }
    }

    override fun getWhiteBalance() {
        when (val result = WhiteBalanceManager.getCurrentWBBySharedPrefs()) {
            EYS_ERROR -> mIPresenter.onWhiteBalance(errorMsg(result), EYS_ERROR)
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedWhiteBalance()
            else -> mIPresenter.onWhiteBalance(value = result)
        }
    }

    override fun getWhiteBalanceLimit() {
        val limit = WhiteBalanceManager.getWBLimitBySharedPrefs()
        mIPresenter.onWhiteBalanceLimit(limit[0], limit[1])
    }

    override fun setWhiteBalance(value: Int) {
        when (val result = WhiteBalanceManager.getCurrentWBBySharedPrefs()) {
            EYS_ERROR -> mIPresenter.onWhiteBalance(errorMsg(result), EYS_ERROR)
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedWhiteBalance()
            else -> WhiteBalanceManager.setSharedPrefs(INDEX_CURRENT_WHITE_BALANCE, value)
        }
    }

    /**
     * Light source
     */

    override fun getLightSource() {
        when (val result = LightSourceManager.getCurrentLSBySharedPrefs()) {
            EYS_ERROR -> mIPresenter.onLightSource(errorMsg(result), EYS_ERROR)
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedLightSource()
            else -> mIPresenter.onLightSource(value = result)
        }
    }

    override fun getLightSourceLimit() {
        val limit = LightSourceManager.getLSLimitBySharedPrefs()
        mIPresenter.onLightSourceLimit(limit[0], limit[1])
    }

    override fun setLightSource(value: Int) {
        when (val result = LightSourceManager.getCurrentLSBySharedPrefs()) {
            EYS_ERROR -> mIPresenter.onLightSource(errorMsg(result), EYS_ERROR)
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedLightSource()
            else -> LightSourceManager.setSharedPrefs(INDEX_CURRENT_LIGHT_SOURCE, value)
        }
    }

    override fun getLowLightCompensation() {
        if (SharedPrefManager.get(KEY.INTERLEAVE_MODE, false) as Boolean) {
            mIPresenter.onLowLightCompensation(errorMsg(INTERLEAVE_MODE_LLC), false)
        } else {
            when (val result = LightSourceManager.getLLCBySharedPrefs()) {
                LOW_LIGHT_COMPENSATION_OFF, LOW_LIGHT_COMPENSATION_ON -> mIPresenter.onLowLightCompensation(
                    enabled = result == LOW_LIGHT_COMPENSATION_ON
                )
                DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedLowLightCompensation()
                else -> mIPresenter.onLowLightCompensation(errorMsg(result), false)
            }
        }
    }

    override fun setLowLightCompensation(enabled: Boolean) {
        when (val result = LightSourceManager.getLLCBySharedPrefs()) {
            LOW_LIGHT_COMPENSATION_OFF, LOW_LIGHT_COMPENSATION_ON -> {
                val value = if (enabled) LOW_LIGHT_COMPENSATION_ON else LOW_LIGHT_COMPENSATION_OFF
                LightSourceManager.setSharedPrefs(INDEX_LOW_LIGHT_COMPENSATION, value)
                mIPresenter.onLowLightCompensation(enabled = enabled)
            }
            DEVICE_NOT_SUPPORT -> mIPresenter.onUnsupportedLowLightCompensation()
            else -> mIPresenter.onLowLightCompensation(errorMsg(result), false)
        }
    }

    /**
     * Reset
     */

    override fun reset() {
        // Exposure
        val result = ExposureManager.defaultSharedPrefs()
        mIPresenter.onAutoExposure(
            errorMsg(result[INDEX_AUTO_EXPOSURE]),
            result[INDEX_AUTO_EXPOSURE] == EXPOSURE_MODE_AUTO_APERTURE
        )
        mIPresenter.onExposureTime(
            errorMsg(result[INDEX_EXPOSURE_ABSOLUTE_TIME]),
            result[INDEX_EXPOSURE_ABSOLUTE_TIME]
        )

        // White balance
        val result2 = WhiteBalanceManager.defaultSharedPrefs()
        mIPresenter.onAutoWhiteBalance(
            errorMsg(result2[INDEX_AUTO_WHITE_BALANCE]),
            result2[INDEX_AUTO_WHITE_BALANCE] == AUTO_WHITE_BALANCE_ON
        )
        mIPresenter.onWhiteBalance(
            errorMsg(result2[INDEX_CURRENT_WHITE_BALANCE]),
            result2[INDEX_CURRENT_WHITE_BALANCE]
        )

        // Light source
        val result3 = LightSourceManager.defaultSharedPrefs()
        mIPresenter.onLightSource(
            errorMsg(result3[INDEX_CURRENT_LIGHT_SOURCE]),
            result3[INDEX_CURRENT_LIGHT_SOURCE]
        )
        if (SharedPrefManager.get(KEY.INTERLEAVE_MODE, false) as Boolean) {
            mIPresenter.onLowLightCompensation(errorMsg(INTERLEAVE_MODE_LLC), false)
        } else {
            mIPresenter.onLowLightCompensation(
                errorMsg(result3[INDEX_LOW_LIGHT_COMPENSATION]),
                result3[INDEX_LOW_LIGHT_COMPENSATION] == LOW_LIGHT_COMPENSATION_ON
            )
        }
    }

    private fun errorMsg(errorCode: Int): String? {
        return when (errorCode) {
            EYS_ERROR -> "EYS_ERROR"
            UVC_ERROR_ACCESS -> "UVC_ERROR_ACCESS"
            DEVICE_FIND_FAIL -> "DEVICE_FIND_FAIL"
            INTERLEAVE_MODE_LLC -> "Interleave mode is on, so the llc is disable"
            else -> null
        }
    }
}
