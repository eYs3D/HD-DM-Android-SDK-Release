package com.esp.uvc.main.settings.sensor

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class PSensorSettings(v: ISensor.View) : ISensor.Presenter, KoinComponent {

    private val mIView = v

    private val mIModel: ISensor.Model by inject { parametersOf(this) }

    override fun getSensorConfiguration() {
        mIModel.isAutoExposure()
        mIModel.getExposureTime()
        mIModel.isAutoWhiteBalance()
        mIModel.getWhiteBalance()
        mIModel.getLightSource()
        mIModel.getLowLightCompensation()
    }

    /**
     * Exposure
     */

    override fun setAutoExposure(enabled: Boolean) {
        mIModel.setAutoExposure(enabled)
    }

    override fun onAutoExposure(error: String?, enabled: Boolean) {
        mIView.onAutoExposure(error, enabled)
    }

    override fun onUnsupportedAutoExposure() {
        mIView.onUnsupportedAutoExposure()
    }

    override fun setExposureTime(value: Int) {
        mIModel.setExposureTime(value)
    }

    override fun onExposureTime(error: String?, value: Int) {
        if (error == null) {
            mIModel.getExposureTimeLimit()
        }
        mIView.onExposureTime(error, value)
    }

    override fun onExposureTimeLimit(min: Int, max: Int) {
        mIView.onExposureTimeLimit(min, max)
    }

    override fun onUnsupportedExposureTime() {
        mIView.onUnsupportedExposureTime()
    }

    /**
     * White balance
     */

    override fun setAutoWhiteBalance(enabled: Boolean) {
        mIModel.setAutoWhiteBalance(enabled)
    }

    override fun onAutoWhiteBalance(error: String?, enabled: Boolean) {
        mIView.onAutoWhiteBalance(error, enabled)
    }

    override fun onUnsupportedAutoWhiteBalance() {
        mIView.onUnsupportedAutoWhiteBalance()
    }

    override fun setWhiteBalance(value: Int) {
        mIModel.setWhiteBalance(value)
    }

    override fun onWhiteBalance(error: String?, value: Int) {
        if (error == null) {
            mIModel.getWhiteBalanceLimit()
        }
        mIView.onWhiteBalance(error, value)
    }

    override fun onUnsupportedWhiteBalance() {
        mIView.onUnsupportedAutoWhiteBalance()
    }

    override fun onWhiteBalanceLimit(min: Int, max: Int) {
        mIView.onWhiteBalanceLimit(min, max)
    }

    /**
     * Light source
     */

    override fun setLightSource(value: Int) {
        mIModel.setLightSource(value)
    }

    override fun onLightSource(error: String?, value: Int) {
        if (error == null) {
            mIModel.getLightSourceLimit()
        }
        mIView.onLightSource(error, value)
    }

    override fun onUnsupportedLightSource() {
        mIView.onUnsupportedLightSource()
    }

    override fun onLightSourceLimit(min: Int, max: Int) {
        mIView.onLightSourceLimit(min, max)
    }

    override fun setLowLightCompensation(enabled: Boolean) {
        mIModel.setLowLightCompensation(enabled)
    }

    override fun onLowLightCompensation(error: String?, enabled: Boolean) {
        mIView.onLowLightCompensation(error, enabled)
    }

    override fun onUnsupportedLowLightCompensation() {
        mIView.onUnsupportedLowLightCompensation()
    }

    /**
     * Reset
     */

    override fun reset() {
        mIModel.reset()
    }
}