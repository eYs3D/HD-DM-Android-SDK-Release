package com.esp.uvc.main.settings.preview_settings

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.utils.logd
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class PPreviewSettings(v: IPreviewSettings.View, usbMonitor: USBMonitor) :
    IPreviewSettings.Presenter, KoinComponent {

    private val mIView = v

    private val mIModel: IPreviewSettings.Model by inject { parametersOf(this, usbMonitor) }

    override fun onStart() {
        mIModel.getOption()
        mIModel.getOrientation()
        mIModel.registerUsbMonitor()
    }

    override fun onStop() {
        mIModel.unregisterUsbMonitor()
        mIModel.destroy()
    }

    override fun onDeviceAttach(device: UsbDevice?) {
        mIModel.onDeviceAttach(device)
    }

    override fun onDeviceDetach(device: UsbDevice?) {
        mIModel.onDeviceDetach(device)
    }

    override fun onDeviceConnect(
        device: UsbDevice?,
        ctrlBlock: USBMonitor.UsbControlBlock?,
        createNew: Boolean
    ) {
        mIModel.onDeviceConnect(device, ctrlBlock, createNew)
    }

    override fun onDeviceDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
        mIModel.onDeviceDisconnect(device, ctrlBlock)
    }

    override fun onModeSelected(modeIndex: Int) {
        mIModel.onModeSelected(modeIndex)
    }

    override fun onColorFpsSelected(colorFpsIndex: Int) {
        mIModel.onColorFpsSelected(colorFpsIndex)
    }

    override fun onDepthFpsSelected(depthFpsIndex: Int) {
        mIModel.onDepthFpsSelected(depthFpsIndex)
    }

    override fun onOption(
        showFps: Boolean,
        /* remove IR UI from settings
        irExtended: Boolean,
        irCurrent: Int,
        */
        roiSizeList: List<String>,
        roiSizeIndex: Int
    ) {
        mIView.onOption(showFps, /*irExtended, irCurrent,*/ roiSizeList, roiSizeIndex)//remove IR UI from settings
    }

    override fun onOrientation(
        reverse: Boolean,
        landscape: Boolean,
        mirror: Boolean,
        plyFilter: Boolean
    ) {
        mIView.onOrientation(reverse, landscape, mirror, plyFilter)
    }

    override fun onModeList(list: List<String>) {
        mIView.onModeList(list)
    }

    override fun onDataTypeList(list: List<String>) {
        mIView.onDataTypeList(list)
    }

    override fun onColorStreamList(list: List<String>?) {
        mIView.onColorStreamList(list)
    }

    override fun onDepthStreamList(list: List<String>?) {
        mIView.onDepthStreamList(list)
    }

    override fun onColorFpsList(list: List<String>?) {
        mIView.onColorFpsList(list)
    }

    override fun onDepthFpsList(list: List<String>?) {
        mIView.onDepthFpsList(list)
    }

    override fun onModeInfo(
        modeIndex: Int,
        depthDataTypeIndex: Int,
        enableColor: Boolean,
        enableDepth: Boolean,
        colorResolutionIndex: Int,
        depthResolutionIndex: Int,
        colorFpsIndex: Int,
        depthFpsIndex: Int
    ) {
        mIView.onModeInfo(
            modeIndex,
            depthDataTypeIndex,
            enableColor,
            enableDepth,
            colorResolutionIndex,
            depthResolutionIndex,
            colorFpsIndex,
            depthFpsIndex
        )
    }

    override fun onColorFpsInfo(index: Int) {
        mIView.onColorFpsInfo(index)
    }

    override fun onDepthFpsInfo(index: Int) {
        mIView.onDepthFpsInfo(index)
    }

    override fun getCameraInfoViewValues(): ArrayList<Any?> {
        return mIView.getCameraInfoViewValues()
    }

    override fun getOptionViewValues(): ArrayList<Any?> {
        return mIView.getOptionViewValues()
    }

    override fun getOtherViewValues(): ArrayList<Any?> {
        return mIView.getOtherViewValues()
    }
}