package com.esp.uvc.main.settings.firmware_table

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.USBMonitor
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class PFirmwareTable(v: IFirmwareTable.View, usbMonitor: USBMonitor) : IFirmwareTable.Presenter,
    KoinComponent {

    private val mIView = v

    private val mIModel: IFirmwareTable.Model by inject { parametersOf(this, usbMonitor) }

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

    override fun onReadRectifyLog(index: Int) {
        mIModel.onReadRectifyLog(index)
    }

    override fun onRectifyLog(index: Int, data: String?) {
        mIView.onRectifyLog(index, data)
    }
}