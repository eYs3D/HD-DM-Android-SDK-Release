package com.esp.uvc.main.settings.preview_settings

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.USBMonitor

interface IPreviewSettings {

    interface Model {

        fun getOption()

        fun getOrientation()

        fun registerUsbMonitor()

        fun unregisterUsbMonitor()

        fun destroy()

        fun onDeviceAttach(device: UsbDevice?)

        fun onDeviceDetach(device: UsbDevice?)

        fun onDeviceConnect(
            device: UsbDevice?,
            ctrlBlock: USBMonitor.UsbControlBlock?,
            createNew: Boolean
        )

        fun onDeviceDisconnect(
            device: UsbDevice?,
            ctrlBlock: USBMonitor.UsbControlBlock?
        )

        fun onModeSelected(modeIndex: Int)

        fun onColorFpsSelected(colorFpsIndex: Int)

        fun onDepthFpsSelected(depthFpsIndex: Int)

    }

    interface View {

        fun onOption(
            showFps: Boolean,
            irExtended: Boolean,
            irCurrent: Int,
            roiSizeList: List<String>,
            roiSizeIndex: Int
        )

        fun onOrientation(reverse: Boolean, landscape: Boolean, mirror: Boolean, plyFilter: Boolean)

        fun onModeList(list: List<String>)

        fun onDataTypeList(list: List<String>)

        fun onColorStreamList(list: List<String>?)

        fun onDepthStreamList(list: List<String>?)

        fun onColorFpsList(list: List<String>?)

        fun onDepthFpsList(list: List<String>?)

        fun onModeInfo(
            modeIndex: Int,
            depthDataTypeIndex: Int,
            enableColor: Boolean,
            enableDepth: Boolean,
            colorResolutionIndex: Int,
            depthResolutionIndex: Int,
            colorFpsIndex: Int,
            depthFpsIndex: Int
        )

        fun onColorFpsInfo(index: Int) // color/depth fps must be the same

        fun onDepthFpsInfo(index: Int)

        fun getCameraInfoViewValues(): ArrayList<Any?>

        fun getOptionViewValues(): ArrayList<Any?>

        fun getOtherViewValues(): ArrayList<Any?>
    }

    interface Presenter {

        fun onStart()

        fun onStop()

        fun onDeviceAttach(device: UsbDevice?)

        fun onDeviceDetach(device: UsbDevice?)

        fun onDeviceConnect(
            device: UsbDevice?,
            ctrlBlock: USBMonitor.UsbControlBlock?,
            createNew: Boolean
        )

        fun onDeviceDisconnect(
            device: UsbDevice?,
            ctrlBlock: USBMonitor.UsbControlBlock?
        )

        fun onModeSelected(modeIndex: Int)

        fun onColorFpsSelected(colorFpsIndex: Int)

        fun onDepthFpsSelected(depthFpsIndex: Int)

        fun onOption(
            showFps: Boolean,
            irExtended: Boolean,
            irCurrent: Int,
            roiSizeList: List<String>,
            roiSizeIndex: Int
        )

        fun onOrientation(reverse: Boolean, landscape: Boolean, mirror: Boolean, plyFilter: Boolean)

        fun onModeList(list: List<String>)

        fun onDataTypeList(list: List<String>)

        fun onColorStreamList(list: List<String>?)

        fun onDepthStreamList(list: List<String>?)

        fun onColorFpsList(list: List<String>?)

        fun onDepthFpsList(list: List<String>?)

        fun onModeInfo(
            modeIndex: Int,
            depthDataTypeIndex: Int,
            enableColor: Boolean,
            enableDepth: Boolean,
            colorResolutionIndex: Int,
            depthResolutionIndex: Int,
            colorFpsIndex: Int,
            depthFpsIndex: Int,
        )

        fun onColorFpsInfo(index: Int) // color/depth fps must be the same

        fun onDepthFpsInfo(index: Int)

        fun getCameraInfoViewValues(): ArrayList<Any?>

        fun getOptionViewValues(): ArrayList<Any?>

        fun getOtherViewValues(): ArrayList<Any?>
    }
}