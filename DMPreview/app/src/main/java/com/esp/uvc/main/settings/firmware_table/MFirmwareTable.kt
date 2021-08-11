package com.esp.uvc.main.settings.firmware_table

import android.hardware.usb.UsbDevice
import android.os.Environment
import com.esp.android.usb.camera.core.ApcCamera
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.camera_modes.CameraModeManager
import com.esp.uvc.utils.loge
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*

private const val APC_CALIB_LOG_FILE_ID_0 = 240

private const val RECTIFY_LOG_READ = "rectify_log_read"

private val DIR_PATH = Environment.getExternalStorageDirectory().toString() + "/eYs3D/"

class MFirmwareTable(p: IFirmwareTable.Presenter, usbMonitor: USBMonitor) : IFirmwareTable.Model {

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

    override fun onReadRectifyLog(index: Int) {
        if (index == -1) {
            mIPresenter.onRectifyLog(-1, null)
            return
        }
        GlobalScope.launch {
            var serialNumber = mCamera!!.serialNumberValue
            val last = serialNumber.last()
            if (last.category == CharCategory.CONTROL) {
                serialNumber = serialNumber!!.substring(0, serialNumber.length - 1)
            }
            val buffer = mCamera!!.getFileData(APC_CALIB_LOG_FILE_ID_0 + index)
            val rectifyLogData = mCamera!!.getRectifyLogData(index)
            if (rectifyLogData == null) {
                mIPresenter.onRectifyLog(-1, null)
            } else {
                val fileName = "${serialNumber}_${RECTIFY_LOG_READ}_$index"
                writeToFile(DIR_PATH, fileName, buffer)
                val fileNameText = "${serialNumber}_${RECTIFY_LOG_READ}_$index.txt"
                writeToFile(DIR_PATH, fileNameText, rectifyLogData.toString())
                mIPresenter.onRectifyLog(index, rectifyLogData.toString())
            }
        }
    }

    private fun writeToFile(dirPath: String, fileName: String, buffer: ByteArray) {
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        } else if (!dir.isDirectory && dir.canWrite()) {
            dir.delete()
            dir.mkdirs()
        }
        val file = File(dirPath + fileName)

        val bos: BufferedOutputStream
        try {
            bos = BufferedOutputStream(FileOutputStream(file))
            bos.write(buffer)
            bos.flush()
            bos.close()
        } catch (e: IOException) {
            loge("writeToFile Exception (ByteArray) : $e")
        }
    }

    private fun writeToFile(dirPath: String, fileName: String, buffer: String) {
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        } else if (!dir.isDirectory && dir.canWrite()) {
            dir.delete()
            dir.mkdirs()
        }
        val file = File(dirPath + fileName)
        val bos: OutputStreamWriter
        try {
            bos = OutputStreamWriter(FileOutputStream(file))
            bos.write(buffer)
            bos.flush()
            bos.close()
        } catch (e: IOException) {
            loge("writeToFile Exception (String) : $e")
        }
    }
}
