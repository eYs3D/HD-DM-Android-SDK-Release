package com.esp.uvc.main.settings.firmware_version

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.ApcCamera
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.camera_modes.CameraModeManager
import com.esp.uvc.utils.loge
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MFirmwareVersion(p: IFirmwareVersion.Presenter, usbMonitor: USBMonitor) : IFirmwareVersion.Model {
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

        val fwp = mCamera!!.isProtectedFlash
        val sti = mCamera!!.getStructLen()
        val unpASS = mCamera!!.getUnpAreaStartSec()
        mIPresenter.onConnected(version, "0X$pid", "0X$vid", sn, fwp, sti, unpASS)
    }

    override fun onFactoryReset() {
        GlobalScope.launch {
            val result = mCamera!!.factoryReset()
            mIPresenter.onFactoryResetFinish(result)
        }
    }

    override fun onRootUser(name: String) {
        GlobalScope.launch {
            val result = mCamera!!.checkCipher(name)
            mIPresenter.onRootUserResult(result)
        }
    }

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    override fun onRead3X(index: Int) {
        GlobalScope.launch {
            val builder = StringBuilder()
            for(i in 0..9) {
                val yOffsetData = mCamera!!.getYOffsetValue(i)
                builder.append("Index: ")
                builder.appendLine(i)
                builder.append(yOffsetData?.toHex())
                builder.appendLine("\n")
            }
            if (builder.toString().isEmpty()) {
                mIPresenter.onRead3XResult(-1, null)
            } else {
                mIPresenter.onRead3XResult(0, builder.toString())
            }
        }
    }

    override fun onWrite3X(index: Int) {
        GlobalScope.launch {
            var result: Int = 0
            val builder = StringBuilder()
            for(i in 0..9) {
                builder.append("Index: ")
                builder.appendLine(i)
                val yOffsetData = mCamera!!.getYOffsetValue(i)
                if (yOffsetData != null) {
                    result = mCamera!!.setYOffsetValue(yOffsetData, i)
                }
                if(result == 0)
                    builder.appendLine("write 3X success")
                else
                    builder.appendLine("write 3X failed")
            }
            mIPresenter.onWrite3XResult(builder.toString())
        }
    }

    override fun onRead4X(index: Int) {
        GlobalScope.launch {
            val builder = StringBuilder()
            for(i in 0..9) {
                val rectifyTableData = mCamera!!.getRectifyTableValue(i)
                builder.append("Index: ")
                builder.appendLine(i)
                builder.append(rectifyTableData?.toHex())
                builder.appendLine("\n")
            }
            if (builder.toString().isEmpty()) {
                mIPresenter.onRead4XResult(-1, null)
            } else {
                mIPresenter.onRead4XResult(0, builder.toString())
            }
        }
    }

    override fun onWrite4X(index: Int) {
        GlobalScope.launch {
            var result: Int = 0
            val builder = StringBuilder()
            for(i in 0..9) {
                builder.append("Index: ")
                builder.appendLine(i)
                val rectifyTableData = mCamera!!.getRectifyTableValue(i)
                if (rectifyTableData != null) {
                    result = mCamera!!.setRectifyTableValue(rectifyTableData, i)
                }
                if(result == 0)
                    builder.appendLine("write 4X success")
                else
                    builder.appendLine("write 4X failed")
            }
            mIPresenter.onWrite4XResult(builder.toString())
        }
    }

    override fun onRead5X(index: Int) {
        GlobalScope.launch {
            val builder = StringBuilder()
            for(i in 0..9) {
                val zdTableData = mCamera!!.getZDTableValue(i, 4)
                builder.append("Index: ")
                builder.appendLine(i)
                if (zdTableData!= null) {
                    /*
                    val bytes = zdTableData.foldIndexed(ByteArray(zdTableData.size)) { i, a, v ->
                        a.apply {
                            set(
                                i,
                                v.toByte()
                            )
                        }
                    }
                     */
                    var bytes = ByteArray(zdTableData.size * 2)
                    val size = zdTableData.size-1
                    for (i in 0..size) {
                        bytes[i*2 + 0] = (zdTableData[i] shr 8).toByte()
                        bytes[i*2 + 1] = (zdTableData[i] and 0xFF).toByte()
                    }
                    builder.append(bytes?.toHex())
                }
                builder.appendLine("\n")
            }
            if (builder.toString().isEmpty()) {
                mIPresenter.onRead5XResult(-1, null)
            } else {
                mIPresenter.onRead5XResult(0, builder.toString())
            }
        }
    }

    override fun onWrite5X(index: Int) {
        GlobalScope.launch {
            var result: Int = 0
            val builder = StringBuilder()
            for(i in 0..9) {
                builder.append("Index: ")
                builder.appendLine(i)
                val zdTableData = mCamera!!.getZDTableValue(i, 4)
                if (zdTableData != null) {
                    var bufferZD = zdTableData.foldIndexed(ByteArray(zdTableData.size)) { i, a, v ->
                        a.apply {
                            set(
                                i,
                                v.toByte()
                            )
                        }
                    }
                    result = mCamera!!.setZDTableValue(bufferZD, i, 4)
                }
                if(result == 0)
                    builder.appendLine("write 5X success")
                else
                    builder.appendLine("write 5X failed")
            }
            mIPresenter.onWrite5XResult(builder.toString())
        }
    }

    override fun onRead24X(index: Int) {
        GlobalScope.launch {
            val builder = StringBuilder()
            for(i in 0..9) {
                val logData = mCamera!!.getLogDataValue(i, 0)
                builder.append("Index: ")
                builder.appendLine(i)
                builder.append(logData?.toHex())
                builder.appendLine("\n")
            }
            if (builder.toString().isEmpty()) {
                mIPresenter.onRead24XResult(-1, null)
            } else {
                mIPresenter.onRead24XResult(0, builder.toString())
            }
        }
    }

    override fun onWrite24X(index: Int) {
        GlobalScope.launch {
            var result: Int = 0
            val builder = StringBuilder()
            for(i in 0..9) {
                builder.append("Index: ")
                builder.appendLine(i)
                val logData = mCamera!!.getLogDataValue(i, 0)
                if (logData != null) {
                    result = mCamera!!.setLogDataValue(logData, i, 0)
                }
                if (result == 0)
                    builder.appendLine("write 24X success")
                else
                    builder.appendLine("write 24X failed")
            }
            mIPresenter.onWrite24XResult(builder.toString())
        }
    }
}
