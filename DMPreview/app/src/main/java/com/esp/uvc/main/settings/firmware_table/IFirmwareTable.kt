package com.esp.uvc.main.settings.firmware_table

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.USBMonitor

interface IFirmwareTable {

    interface Model {

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

        fun onReadRectifyLog(index: Int)

    }

    interface View {

        fun onConnected()

        fun onConnectionFailed()

        fun onRectifyLog(index: Int, data: String?)

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

        fun onConnected()

        fun onConnectionFailed()

        fun onReadRectifyLog(index: Int)

        fun onRectifyLog(index: Int, data: String?)
    }
}