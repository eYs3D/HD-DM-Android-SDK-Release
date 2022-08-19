package com.esp.uvc.main.settings.firmware_version

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esp.android.usb.camera.core.ApcCamera
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.BaseFragment
import com.esp.uvc.R
import com.esp.uvc.main.common.ProgressDialogFragment
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
    private val mProgressDialog by lazy { ProgressDialogFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseFragment.applyScreenRotation(this)
        setContentView(R.layout.activity_firmware_version)
        tv_firmware_factoryReset.setOnClickListener(mOnClickListener)
        tv_root_text_input.setOnClickListener(mOnClickListener)
        tv_read3x.setOnClickListener(mOnClickListener)
        tv_write3x.setOnClickListener(mOnClickListener)
        tv_read4x.setOnClickListener(mOnClickListener)
        tv_write4x.setOnClickListener(mOnClickListener)
        tv_read5x.setOnClickListener(mOnClickListener)
        tv_write5x.setOnClickListener(mOnClickListener)
        tv_read24x.setOnClickListener(mOnClickListener)
        tv_write24x.setOnClickListener(mOnClickListener)
    }

    override fun onStart() {
        super.onStart()
        mIPresenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        mIPresenter.onStop()
    }

    override fun onConnected(version: String, pid: String, vid: String, sn: String, fwp: Boolean, sti: Int, unpASS: Int) = roUI {
        tv_firmware_version_value.text = version
        tv_product_id_value.text = pid
        tv_vendor_id_value.text = vid
        tv_serial_number_value.text = sn

        tv_firmware_protect_value.text = if (fwp) "True" else "False"
        tv_firmware_structLen_value.text = sti.toString()
        tv_firmware_unpAreaStartSec_value.text = unpASS.toString()
        if (fwp) {
            tv_firmware_factoryReset.isEnabled = true
            tv_root_text_input.isEnabled = true
            tv_read3x.isEnabled = true
            tv_write3x.isEnabled = true
            tv_read4x.isEnabled = true
            tv_write4x.isEnabled = true
            tv_read5x.isEnabled = true
            tv_write5x.isEnabled = true
            tv_read24x.isEnabled = true
            tv_write24x.isEnabled = true
        }
    }

    override fun onConnectionFailed() = roUI {
        tv_firmware_version_value.text = getString(R.string.firmware_version_value_unknown)
        tv_product_id_value.text = getString(R.string.firmware_version_value_unknown)
        tv_vendor_id_value.text = getString(R.string.firmware_version_value_unknown)
        tv_serial_number_value.text = getString(R.string.firmware_version_value_unknown)

        tv_firmware_protect_value.text = getString(R.string.firmware_protect_value_unknown)
        tv_firmware_structLen_value.text = getString(R.string.firmware_StructLen_value_unknown)
        tv_firmware_unpAreaStartSec_value.text = getString(R.string.firmware_UnpAreaStartSec_value_unknown)
        mProgressDialog.dismiss()

        Toast.makeText(this, R.string.camera_error_io, Toast.LENGTH_SHORT).show()
    }
    fun onDisplayAlertDialog(textId: Int, msg: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(textId)
        builder.setMessage(msg)
        builder.setPositiveButton(android.R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.black))
        }
        dialog.show()
    }

    override fun onFactoryResetFinish(result: Int) = roUI {
        mProgressDialog.dismiss()
        if (result == 0)
            Toast.makeText(
                this,
                R.string.firmware_factoryReset_result_success,
                Toast.LENGTH_SHORT
            ).show()
        else
            Toast.makeText(
                this,
                R.string.firmware_factoryReset_result_fail,
                Toast.LENGTH_SHORT
            ).show()
    }
    override fun onRootUserResult(result: Int) = roUI {
        mProgressDialog.dismiss()
        if (result == 0) {
            Toast.makeText(
                this,
                R.string.firmware_root_user_result_success,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                R.string.firmware_root_user_result_failed,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    override fun onRead3XResult(result: Int, data: String?) = roUI {
        mProgressDialog.dismiss()
        if (data == null || result != 0) {
            Toast.makeText(
                this,
                R.string.firmware_read3X_failed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            onDisplayAlertDialog(R.string.firmware_read3X, getString(R.string.firmware_read3X_log_dialog_message, data))
        }
    }

    override fun onWrite3XResult(data: String?) = roUI {
        mProgressDialog.dismiss()
        if (data.isNullOrEmpty()) {
            Toast.makeText(
                this,
                R.string.firmware_write3X_failed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            onDisplayAlertDialog(R.string.firmware_write3X, getString(R.string.firmware_write3X_log_dialog_message, data))
        }
    }

    override fun onRead4XResult(result: Int, data: String?) = roUI {
        mProgressDialog.dismiss()
        if (data == null || result != 0) {
            Toast.makeText(
                this,
                R.string.firmware_read4X_failed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            onDisplayAlertDialog(R.string.firmware_read4X, getString(R.string.firmware_read4X_log_dialog_message, data))
        }
    }

    override fun onWrite4XResult(data: String?) = roUI {
        mProgressDialog.dismiss()
        if(data.isNullOrEmpty()){
            Toast.makeText(
                this,
                R.string.firmware_write4X_failed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            onDisplayAlertDialog(R.string.firmware_write4X, getString(R.string.firmware_write4X_log_dialog_message, data))
        }
    }

    override fun onRead5XResult(result: Int, data: String?) = roUI {
        mProgressDialog.dismiss()
        if (data == null || result != 0) {
            Toast.makeText(
                this,
                R.string.firmware_read5X_failed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            onDisplayAlertDialog(R.string.firmware_read5X, getString(R.string.firmware_read5X_log_dialog_message, data))
        }
    }

    override fun onWrite5XResult(data: String?) = roUI {
        mProgressDialog.dismiss()
        if(data.isNullOrEmpty()){
            Toast.makeText(
                this,
                R.string.firmware_write5X_failed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            onDisplayAlertDialog(R.string.firmware_write5X, getString(R.string.firmware_write5X_log_dialog_message, data))
        }
    }

    override fun onRead24XResult(result: Int, data: String?) = roUI {
        mProgressDialog.dismiss()
        if (data == null || result != 0) {
            Toast.makeText(
                this,
                R.string.firmware_read24X_failed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            onDisplayAlertDialog(R.string.firmware_read24X, getString(R.string.firmware_read24X_log_dialog_message, data))
        }
    }

    override fun onWrite24XResult(data: String?) = roUI {
        mProgressDialog.dismiss()
        if(data.isNullOrEmpty()){
            Toast.makeText(
                this,
                R.string.firmware_write24X_failed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            onDisplayAlertDialog(R.string.firmware_write24X, getString(R.string.firmware_write24X_log_dialog_message, data))
        }
    }

    private val mOnClickListener = View.OnClickListener {
        when (it.id) {
            R.id.tv_firmware_factoryReset -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.firmware_factoryReset)
                    .setMessage(R.string.firmware_factoryReset_msg)
                    .setPositiveButton(R.string.dlg_ok) { _, _ -> mProgressDialog.show(supportFragmentManager, "progressDialog"); mIPresenter.onFactoryReset() }
                    .setNeutralButton(R.string.dlg_cancel) { _, _ -> Toast.makeText(applicationContext, "bye bye", Toast.LENGTH_SHORT).show() }
                    .show()
            }
            R.id.tv_root_text_input -> {
                val item: View = LayoutInflater.from(this).inflate(R.layout.dialog, null)
                AlertDialog.Builder(this)
                    .setTitle(R.string.firmware_root_text)
                    .setView(item)
                    .setPositiveButton(R.string.dlg_ok) { _, _ ->
                        val editText = item.findViewById(R.id.editText_intent_event) as EditText
                        val name = editText.text.toString()
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(applicationContext, R.string.firmware_root_user_result_failed, Toast.LENGTH_SHORT).show()
                        } else {
                            mProgressDialog.show(supportFragmentManager, "progressDialog")
                            mIPresenter.onRootUser(name)
                        }
                    }
                    .show()
            }
            R.id.tv_read3x -> {
                mProgressDialog.show(supportFragmentManager, "progressDialog");
                mIPresenter.onRead3X(0)
            }
            R.id.tv_write3x -> {
                mProgressDialog.show(supportFragmentManager, "progressDialog");
                mIPresenter.onWrite3X(0)
            }
            R.id.tv_read4x -> {
                mProgressDialog.show(supportFragmentManager, "progressDialog");
                mIPresenter.onRead4X(0)
            }
            R.id.tv_write4x -> {
                mProgressDialog.show(supportFragmentManager, "progressDialog");
                mIPresenter.onWrite4X(0)
            }
            R.id.tv_read5x -> {
                mProgressDialog.show(supportFragmentManager, "progressDialog");
                mIPresenter.onRead5X(0)
            }
            R.id.tv_write5x -> {
                mProgressDialog.show(supportFragmentManager, "progressDialog");
                mIPresenter.onWrite5X(0)
            }
            R.id.tv_read24x -> {
                mProgressDialog.show(supportFragmentManager, "progressDialog");
                mIPresenter.onRead24X(0)
            }
            R.id.tv_write24x -> {
                mProgressDialog.show(supportFragmentManager, "progressDialog");
                mIPresenter.onWrite24X(0)
            }
        }
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

            tv_firmware_protect_value.text = getString(R.string.firmware_protect_value_unknown)
            tv_firmware_structLen_value.text = getString(R.string.firmware_StructLen_value_unknown)
            tv_firmware_unpAreaStartSec_value.text = getString(R.string.firmware_UnpAreaStartSec_value_unknown)
            mProgressDialog.dismiss()
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

                tv_firmware_protect_value.text = getString(R.string.firmware_protect_value_unknown)
                tv_firmware_structLen_value.text = getString(R.string.firmware_StructLen_value_unknown)
                tv_firmware_unpAreaStartSec_value.text = getString(R.string.firmware_UnpAreaStartSec_value_unknown)
                mProgressDialog.dismiss()
            }

        override fun onCancel() {
        }
    }
}