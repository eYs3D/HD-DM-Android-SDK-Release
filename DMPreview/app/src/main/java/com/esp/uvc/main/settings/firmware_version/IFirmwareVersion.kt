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

        fun onFactoryReset()
        fun onRootUser(name: String)
        fun onRead3X(index: Int)
        fun onWrite3X(index: Int)
        fun onRead4X(index: Int)
        fun onWrite4X(index: Int)
        fun onRead5X(index: Int)
        fun onWrite5X(index: Int)
        fun onRead24X(index: Int)
        fun onWrite24X(index: Int)

    }

    interface View {

        fun onConnected(version: String, pid: String, vid: String, sn: String, fwp: Boolean, sti: Int, unpASS: Int)

        fun onConnectionFailed()

        fun onFactoryResetFinish(result: Int)
        fun onRootUserResult(result: Int)
        fun onRead3XResult(result: Int, data: String?)
        fun onWrite3XResult(data: String?)
        fun onRead4XResult(result: Int, data: String?)
        fun onWrite4XResult(data: String?)
        fun onRead5XResult(result: Int, data: String?)
        fun onWrite5XResult(data: String?)
        fun onRead24XResult(result: Int, data: String?)
        fun onWrite24XResult(data: String?)

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

        fun onConnected(version: String, pid: String, vid: String, sn: String, fwp: Boolean, sti: Int, unpASS: Int)

        fun onConnectionFailed()
        fun onFactoryReset()
        fun onFactoryResetFinish(result: Int)
        fun onRootUser(name: String)
        fun onRootUserResult(result: Int)
        fun onRead3X(index: Int)
        fun onWrite3X(index: Int)
        fun onRead4X(index: Int)
        fun onWrite4X(index: Int)
        fun onRead5X(index: Int)
        fun onWrite5X(index: Int)
        fun onRead24X(index: Int)
        fun onWrite24X(index: Int)
        fun onRead3XResult(result: Int, data: String?)
        fun onWrite3XResult(data: String?)
        fun onRead4XResult(result: Int, data: String?)
        fun onWrite4XResult(data: String?)
        fun onRead5XResult(result: Int, data: String?)
        fun onWrite5XResult(data: String?)
        fun onRead24XResult(result: Int, data: String?)
        fun onWrite24XResult(data: String?)
    }
}