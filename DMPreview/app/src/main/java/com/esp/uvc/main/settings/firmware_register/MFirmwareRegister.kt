package com.esp.uvc.main.settings.firmware_register

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.ApcCamera
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.camera_modes.CameraModeManager
import com.esp.uvc.utils.loge

// match layout tag
private const val REGISTER_TYPE_FW = "FW"
private const val REGISTER_TYPE_ASIC = "ASIC"
private const val REGISTER_TYPE_I2C = "I2C"

class MFirmwareRegister(p: IFirmwareRegister.Presenter, usbMonitor: USBMonitor) :
    IFirmwareRegister.Model {

    private val mIPresenter = p
    private val mUsbMonitor = usbMonitor

    private var mCamera: ApcCamera? = null

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
            mCamera = ApcCamera()
            if (mCamera!!.open(ctrlBlock) == ApcCamera.EYS_OK) {
                mIPresenter.onConnected()
            } else {
                mIPresenter.onConnectionFailed()
                destroy()
            }
        }
    }

    override fun onDeviceDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
        destroy()
    }

    override fun onGet(
        type: String,
        address2Bytes: Boolean,
        value2Bytes: Boolean,
        i2cSlaveId: Int,
        address: Int
    ) {
        if (type == REGISTER_TYPE_I2C && i2cSlaveId == 0) {
            mIPresenter.onSlaveAddressNotSet()
            return
        }
        if (address == 0) {
            mIPresenter.onAddressNotSet()
            return
        }
        val ret = arrayOfNulls<String>(1)
        when (type) {
            REGISTER_TYPE_FW -> mCamera!!.getFWRegisterValue(ret, address)
            REGISTER_TYPE_ASIC -> mCamera!!.getHWRegisterValue(ret, address)
            REGISTER_TYPE_I2C -> {
                var flag =
                    if (address2Bytes) ApcCamera.FG_Address_2Byte else ApcCamera.FG_Address_1Byte
                flag =
                    flag or if (value2Bytes) ApcCamera.FG_Value_2Byte else ApcCamera.FG_Value_1Byte
                mCamera!!.getSensorRegisterValue(ret, i2cSlaveId, address, flag)
            }
        }
        mIPresenter.onGetResult(ret[0]?.toInt(16) ?: 0)
    }

    override fun onSet(
        type: String,
        address2Bytes: Boolean,
        value2Bytes: Boolean,
        i2cSlaveId: Int,
        address: Int,
        value: Int
    ) {
        if (type == REGISTER_TYPE_I2C && i2cSlaveId == 0) {
            mIPresenter.onSlaveAddressNotSet()
            return
        }
        if (address == 0) {
            mIPresenter.onAddressNotSet()
            return
        }
        if (value == 0) {
            mIPresenter.onValueNotSet()
            return
        }
        var ret: Int = -1
        when (type) {
            REGISTER_TYPE_FW -> ret = mCamera!!.SetFWRegisterValue(address, value)
            REGISTER_TYPE_ASIC -> ret = mCamera!!.setHWRegisterValue(address, value)
            REGISTER_TYPE_I2C -> {
                var flag =
                    if (address2Bytes) ApcCamera.FG_Address_2Byte else ApcCamera.FG_Address_1Byte
                flag =
                    flag or if (value2Bytes) ApcCamera.FG_Value_2Byte else ApcCamera.FG_Value_1Byte
                ret = mCamera!!.setSensorRegisterValue(i2cSlaveId, address, value, flag)
            }
        }
        mIPresenter.onSetResult(ret)
    }
}
