package com.esp.uvc.main.settings.firmware_version

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.EtronCamera
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.camera_modes.CameraModeManager
import com.esp.uvc.utils.loge

class MFirmwareVersion(p: IFirmwareVersion.Presenter, usbMonitor: USBMonitor) :
    IFirmwareVersion.Model {

    private val mIPresenter = p
    private val mUsbMonitor = usbMonitor

    private var mCamera: EtronCamera? = null

    override fun registerUsbMonitor() {
        mUsbMonitor.register()
    }

    override fun unregisterUsbMonitor() {
        mUsbMonitor.unregister()
    }

    override fun destroy() {
        mCamera?.destroy()
        mCamera = null
    }

    override fun onDeviceAttach(device: UsbDevice?) {
        if (device != null && CameraModeManager.SUPPORTED_PID_LIST.contains(device.productId)) {
            mUsbMonitor.requestPermission(device)
        } else {
            mUsbMonitor.deviceList.forEach {
                if (CameraModeManager.SUPPORTED_PID_LIST.contains(it!!.productId)) {
                    mUsbMonitor.requestPermission(it)
                }
            }
        }
    }

    override fun onDeviceDetach(device: UsbDevice?) {
        destroy()
    }

    override fun onDeviceConnect(
        device: UsbDevice?,
        ctrlBlock: USBMonitor.UsbControlBlock?,
        createNew: Boolean
    ) {
        if (ctrlBlock!!.isIMU)
            return
        if (mCamera != null) {
            loge("Camera exists, not re-creating")
        } else {
            mCamera = EtronCamera()
            if (mCamera!!.open(ctrlBlock) == EtronCamera.EYS_OK) {
                setDeviceInfo()
            } else {
                mIPresenter.onConnectionFailed()
                destroy()
            }
        }
    }

    override fun onDeviceDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
        destroy()
    }

    private fun setDeviceInfo() {
        val version = mCamera!!.fwVersionValue
        val pid = mCamera!!.pidValue
        val vid = mCamera!!.vidValue
        val sn = mCamera!!.serialNumberValue
        mIPresenter.onConnected(version, "0X$pid", "0X$vid", sn)
    }
}