package com.esp.uvc.main.settings.firmware_version

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.USBMonitor
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class PFirmwareVersion(v: IFirmwareVersion.View, usbMonitor: USBMonitor) :
    IFirmwareVersion.Presenter,
    KoinComponent {

    private val mIView = v

    private val mIModel: IFirmwareVersion.Model by inject { parametersOf(this, usbMonitor) }

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

    override fun onConnected(version: String, pid: String, vid: String, sn: String) {
        mIView.onConnected(version, pid, vid, sn)
    }

    override fun onConnectionFailed() {
        mIView.onConnectionFailed()
    }
}