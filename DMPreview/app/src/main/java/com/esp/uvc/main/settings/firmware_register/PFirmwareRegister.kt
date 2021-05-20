package com.esp.uvc.main.settings.firmware_register

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.USBMonitor
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class PFirmwareRegister(v: IFirmwareRegister.View, usbMonitor: USBMonitor) :
    IFirmwareRegister.Presenter, KoinComponent {

    private val mIView = v

    private val mIModel: IFirmwareRegister.Model by inject { parametersOf(this, usbMonitor) }

    override fun onStart() {
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

    override fun onConnected() {
        mIView.onConnected()
    }

    override fun onConnectionFailed() {
        mIView.onConnectionFailed()
    }

    override fun onGet(
        type: String,
        address2Bytes: Boolean,
        value2Bytes: Boolean,
        i2cSlaveId: Int,
        address: Int
    ) {
        mIModel.onGet(type, address2Bytes, value2Bytes, i2cSlaveId, address)
    }

    override fun onSet(
        type: String,
        address2Bytes: Boolean,
        value2Bytes: Boolean,
        i2cSlaveId: Int,
        address: Int,
        value: Int
    ) {
        mIModel.onSet(type, address2Bytes, value2Bytes, i2cSlaveId, address, value)
    }

    override fun onAddressNotSet() {
        mIView.onAddressNotSet()
    }

    override fun onValueNotSet() {
        mIView.onValueNotSet()
    }

    override fun onSlaveAddressNotSet() {
        mIView.onSlaveAddressNotSet()
    }

    override fun onSetResult(result: Int) {
        mIView.onSetResult(result)
    }

    override fun onGetResult(result: Int) {
        mIView.onGetResult(result)
    }
}