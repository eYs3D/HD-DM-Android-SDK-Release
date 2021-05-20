package com.esp.uvc.main.settings.firmware_register

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.BaseFragment
import com.esp.uvc.R
import com.esp.uvc.utils.roUI
import kotlinx.android.synthetic.main.activity_firmware_register.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class FirmwareRegisterActivity : AppCompatActivity(), IFirmwareRegister.View {

    companion object {
        fun startInstance(context: Context) {
            context.startActivity(Intent(context, FirmwareRegisterActivity::class.java))
        }
    }

    private val mIPresenter: IFirmwareRegister.Presenter by inject {
        parametersOf(
            this,
            USBMonitor(this, mOnDeviceConnectListener)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseFragment.applyScreenRotation(this)
        setContentView(R.layout.activity_firmware_register)
        rg_register_type.setOnCheckedChangeListener(mCheckedChangeListener)
        layout_i2c_slave_id.isEnabled = false // not work in layout.xml
        bt_get.setOnClickListener(mOnClickListener)
        bt_set.setOnClickListener(mOnClickListener)
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
        bt_get.isEnabled = true
        bt_set.isEnabled = true
    }

    override fun onConnectionFailed() = roUI {
        bt_get.isEnabled = false
        bt_set.isEnabled = false
        Toast.makeText(this, R.string.camera_error_io, Toast.LENGTH_SHORT).show()
    }

    override fun onAddressNotSet() = roUI {
        Toast.makeText(this, R.string.general_address_not_set_toast, Toast.LENGTH_SHORT).show()
    }

    override fun onValueNotSet() = roUI {
        Toast.makeText(this, R.string.general_value_not_set_toast, Toast.LENGTH_SHORT).show()
    }

    override fun onSlaveAddressNotSet() = roUI {
        Toast.makeText(this, R.string.general_slave_address_not_set_toast, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onSetResult(result: Int) = roUI {
        Toast.makeText(
            this,
            getString(R.string.general_set_register_result, result),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onGetResult(result: Int) = roUI {
        text_input_value.setNumber(result)
    }

    private val mCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { _, checkedId ->
            cb_address_2_bytes.isEnabled = checkedId == R.id.rb_i2c_register
            cb_address_2_bytes.isChecked = checkedId == R.id.rb_asic_register
            cb_value_2_bytes.isEnabled = checkedId == R.id.rb_i2c_register
            cb_value_2_bytes.isChecked = false
            layout_i2c_slave_id.isEnabled = checkedId == R.id.rb_i2c_register
            text_input_i2c_slave_id.setText("")
        }

    private val mOnClickListener = View.OnClickListener {
        when (it.id) {
            R.id.bt_get -> {
                mIPresenter.onGet(
                    rg_register_type.findViewById<RadioButton>(rg_register_type.checkedRadioButtonId).tag as String,
                    cb_address_2_bytes.isChecked,
                    cb_value_2_bytes.isChecked,
                    text_input_i2c_slave_id.getNumber(),
                    text_input_address.getNumber()
                )
            }
            R.id.bt_set -> {
                mIPresenter.onSet(
                    rg_register_type.findViewById<RadioButton>(rg_register_type.checkedRadioButtonId).tag as String,
                    cb_address_2_bytes.isChecked,
                    cb_value_2_bytes.isChecked,
                    text_input_i2c_slave_id.getNumber(),
                    text_input_address.getNumber(),
                    text_input_value.getNumber()
                )
            }
        }
    }

    private val mOnDeviceConnectListener = object : USBMonitor.OnDeviceConnectListener {

        override fun onAttach(device: UsbDevice?) {
            mIPresenter.onDeviceAttach(device)
        }

        override fun onDetach(device: UsbDevice?) = roUI {
            bt_get.isEnabled = false
            bt_set.isEnabled = false
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
                bt_get.isEnabled = false
                bt_set.isEnabled = false
                mIPresenter.onDeviceDisconnect(device, ctrlBlock)
            }

        override fun onCancel() {
        }
    }
}