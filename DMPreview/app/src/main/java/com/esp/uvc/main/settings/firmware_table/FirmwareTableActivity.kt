package com.esp.uvc.main.settings.firmware_table

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.BaseFragment
import com.esp.uvc.R
import com.esp.uvc.main.common.ProgressDialogFragment
import com.esp.uvc.utils.roUI
import kotlinx.android.synthetic.main.activity_firmware_table.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class FirmwareTableActivity : AppCompatActivity(), IFirmwareTable.View {

    companion object {
        fun startInstance(context: Context) {
            context.startActivity(Intent(context, FirmwareTableActivity::class.java))
        }
    }

    private val mIPresenter: IFirmwareTable.Presenter by inject {
        parametersOf(
            this,
            USBMonitor(this, mOnDeviceConnectListener)
        )
    }

    private lateinit var mAdapter: ArrayAdapter<Int>

    private val mProgressDialog by lazy { ProgressDialogFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseFragment.applyScreenRotation(this)
        setContentView(R.layout.activity_firmware_table)
        mAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ArrayList<Int>())
        spinner_index.adapter = mAdapter
        tv_rectify_log_read.setOnClickListener(mOnClickListener)
    }

    override fun onStart() {
        super.onStart()
        mIPresenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        mIPresenter.onStop()
    }

    override fun onConnected() = roUI {
        mAdapter.clear()
        mAdapter.addAll(Array(10) { it }.toList())
    }

    override fun onConnectionFailed() = roUI {
        mAdapter.clear()
        mProgressDialog.dismiss()
        Toast.makeText(this, R.string.camera_error_io, Toast.LENGTH_SHORT).show()
    }

    override fun onRectifyLog(index: Int, data: String?) = roUI {
        mProgressDialog.dismiss()
        if (data == null) {
            Toast.makeText(
                this,
                R.string.firmware_table_failed_to_read_rectify_log,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val msg = getString(R.string.firmware_table_rectify_log_dialog_message, index, data)
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.rectify_log_read_title)
            builder.setMessage(msg)
            builder.setPositiveButton(android.R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(R.color.black))
            }
            dialog.show()
        }
    }

    private val mOnClickListener = View.OnClickListener {
        mProgressDialog.show(supportFragmentManager, "progressDialog")
        mIPresenter.onReadRectifyLog(spinner_index.selectedItemPosition)
    }

    private val mOnDeviceConnectListener = object : USBMonitor.OnDeviceConnectListener {

        override fun onAttach(device: UsbDevice?) {
            mIPresenter.onDeviceAttach(device)
        }

        override fun onDetach(device: UsbDevice?) = roUI {
            mAdapter.clear()
            mProgressDialog.dismiss()
            mIPresenter.onDeviceDetach(device)
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
                mAdapter.clear()
                mProgressDialog.dismiss()
                mIPresenter.onDeviceDisconnect(device, ctrlBlock)
            }

        override fun onCancel() {
        }
    }
}
