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

    override fun onConnected(version: String, pid: String, vid: String, sn: String, fwp: Boolean, sti: Int, unpASS: Int) {
        mIView.onConnected(version, pid, vid, sn, fwp, sti, unpASS)
    }

    override fun onConnectionFailed() {
        mIView.onConnectionFailed()
    }

    override fun onFactoryReset() {
        mIModel.onFactoryReset()
    }

    override fun onFactoryResetFinish(result: Int) {
        mIView.onFactoryResetFinish(result)
    }

    override fun onRootUser(name: String) {
        mIModel.onRootUser(name)
    }

    override fun onRootUserResult(result: Int) {
        mIView.onRootUserResult(result)
    }

    override fun onRead3X(index: Int) {
        mIModel.onRead3X(index)
    }

    override fun onRead3XResult(result: Int, data: String?) {
        mIView.onRead3XResult(result, data)
    }

    override fun onWrite3X(index: Int) {
        mIModel.onWrite3X(index)
    }

    override fun onWrite3XResult(data: String?) {
        mIView.onWrite3XResult(data)
    }

    override fun onRead4X(index: Int) {
        mIModel.onRead4X(index)
    }

    override fun onRead4XResult(result: Int, data: String?) {
        mIView.onRead4XResult(result, data)
    }

    override fun onWrite4X(index: Int) {
        mIModel.onWrite4X(index)
    }

    override fun onWrite4XResult(data: String?) {
        mIView.onWrite4XResult(data)
    }

    override fun onRead5X(index: Int) {
        mIModel.onRead5X(index)
    }

    override fun onRead5XResult(result: Int, data: String?) {
        mIView.onRead5XResult(result, data)
    }

    override fun onWrite5X(index: Int) {
        mIModel.onWrite5X(index)
    }

    override fun onWrite5XResult(data: String?) {
        mIView.onWrite5XResult(data)
    }

    override fun onRead24X(index: Int) {
        mIModel.onRead24X(index)
    }

    override fun onRead24XResult(result: Int, data: String?) {
        mIView.onRead24XResult(result, data)
    }

    override fun onWrite24X(index: Int) {
        mIModel.onWrite24X(index)
    }

    override fun onWrite24XResult(data: String?) {
        mIView.onWrite24XResult(data)
    }
}