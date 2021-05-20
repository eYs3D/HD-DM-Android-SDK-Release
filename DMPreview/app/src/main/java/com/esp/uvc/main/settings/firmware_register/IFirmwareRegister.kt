package com.esp.uvc.main.settings.firmware_register

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.USBMonitor

interface IFirmwareRegister {

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

        fun onGet(
            type: String,
            address2Bytes: Boolean,
            value2Bytes: Boolean,
            i2cSlaveId: Int,
            address: Int
        )

        fun onSet(
            type: String,
            address2Bytes: Boolean,
            value2Bytes: Boolean,
            i2cSlaveId: Int,
            address: Int,
            value: Int
        )

    }

    interface View {

        fun onConnected()

        fun onConnectionFailed()

        fun onAddressNotSet()

        fun onValueNotSet()

        fun onSlaveAddressNotSet()

        fun onSetResult(result: Int)

        fun onGetResult(result: Int)

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

        fun onGet(
            type: String,
            address2Bytes: Boolean,
            value2Bytes: Boolean,
            i2cSlaveId: Int,
            address: Int
        )

        fun onSet(
            type: String,
            address2Bytes: Boolean,
            value2Bytes: Boolean,
            i2cSlaveId: Int,
            address: Int,
            value: Int
        )

        fun onAddressNotSet()

        fun onValueNotSet()

        fun onSlaveAddressNotSet()

        fun onSetResult(result: Int)

        fun onGetResult(result: Int)
    }
}