package com.esp.uvc.main.settings.firmware_version

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.BaseFragment
import com.esp.uvc.R
import com.esp.uvc.utils.roUI
import kotlinx.android.synthetic.main.activity_firmware_version.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class FirmwareVersionActivity : AppCompatActivity(), IFirmwareVersion.View {

    companion object {
        fun startInstance(context: Context) {
            context.startActivity(Intent(context, FirmwareVersionActivity::class.java))
        }
    }

    private val mIPresenter: IFirmwareVersion.Presenter by inject {
        parametersOf(
            this,
            USBMonitor(this, mOnDeviceConnectListener)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseFragment.applyScreenRotation(this)
        setContentView(R.layout.activity_firmware_version)
    }

    override fun onStart() {
        super.onStart()
        mIPresenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        mIPresenter.onStop()
    }

    override fun onConnected(version: String, pid: String, vid: String, sn: String) = roUI {
        tv_firmware_version_value.text = version
        tv_product_id_value.text = pid
        tv_vendor_id_value.text = vid
        tv_serial_number_value.text = sn
    }

    override fun onConnectionFailed() = roUI {
        tv_firmware_version_value.text = getString(R.string.firmware_version_value_unknown)
        tv_product_id_value.text = getString(R.string.firmware_version_value_unknown)
        tv_vendor_id_value.text = getString(R.string.firmware_version_value_unknown)
        tv_serial_number_value.text = getString(R.string.firmware_version_value_unknown)
        Toast.makeText(this, R.string.camera_error_io, Toast.LENGTH_SHORT).show()
    }

    private val mOnDeviceConnectListener = object : USBMonitor.OnDeviceConnectListener {

        override fun onAttach(device: UsbDevice?) {
            mIPresenter.onDeviceAttach(device)
        }

        override fun onDetach(device: UsbDevice?) = roUI {
            mIPresenter.onDeviceDetach(device)
            tv_firmware_version_value.text = getString(R.string.firmware_version_value_unknown)
            tv_product_id_value.text = getString(R.string.firmware_version_value_unknown)
            tv_vendor_id_value.text = getString(R.string.firmware_version_value_unknown)
            tv_serial_number_value.text = getString(R.string.firmware_version_value_unknown)
        }

        override fun onConnect(
            device: UsbDevice?,
            ctrlBlock: USBMonitor.UsbControlBlock?,
            createNew: Boolean
        ) {
            mIPresenter.onDeviceConnect(device, ctrlBlock, createNew)
        }

        override fun onDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) =
            roUI {
                mIPresenter.onDeviceDisconnect(device, ctrlBlock)
                tv_firmware_version_value.text = getString(R.string.firmware_version_value_unknown)
                tv_product_id_value.text = getString(R.string.firmware_version_value_unknown)
                tv_vendor_id_value.text = getString(R.string.firmware_version_value_unknown)
                tv_serial_number_value.text = getString(R.string.firmware_version_value_unknown)
            }

        override fun onCancel() {
        }
    }
}