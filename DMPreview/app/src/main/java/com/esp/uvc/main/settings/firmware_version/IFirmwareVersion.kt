package com.esp.uvc.main.settings.firmware_version

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.USBMonitor

interface IFirmwareVersion {

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

    }

    interface View {

        fun onConnected(version: String, pid: String, vid: String, sn: String)

        fun onConnectionFailed()

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

        fun onConnected(version: String, pid: String, vid: String, sn: String)

        fun onConnectionFailed()
    }
}