package com.esp.uvc.main.settings.sensor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esp.uvc.BaseFragment
import com.esp.uvc.R
import com.esp.uvc.utils.roUI
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.activity_sensor_settings.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * Need to open the camera to load the sensor data first.
 * Only save the sensor data to "SharedPreferences".
  */
class SensorSettingsActivity : AppCompatActivity(), ISensor.View {

    companion object {
        fun startInstance(context: Context) {
            context.startActivity(Intent(context, SensorSettingsActivity::class.java))
        }
    }

    private val mIPresenter: ISensor.Presenter by inject { parametersOf(this) }

    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseFragment.applyScreenRotation(this)
        setContentView(R.layout.activity_sensor_settings)
        cb_auto_exposure.setOnCheckedChangeListener(mOnCheckedListener)
        seekBar_exposure_time.onSeekChangeListener = mOnSeekChangeListener
        cb_auto_white_balance.setOnCheckedChangeListener(mOnCheckedListener)
        seekBar_white_balance.onSeekChangeListener = mOnSeekChangeListener
        rg_light_source.setOnCheckedChangeListener(mOnRadioGroupCheckedChangeListener)
        cb_low_light_compensation.setOnCheckedChangeListener(mOnCheckedListener)
        bt_reset.setOnClickListener(mOnClickListener)
    }

    override fun onResume() {
        super.onResume()
        mIPresenter.getSensorConfiguration()
    }

    /**
     * Exposure
     */

    override fun onAutoExposure(error: String?, enabled: Boolean) {
        if (error == null) {
            cb_auto_exposure.isEnabled = true
            cb_auto_exposure.isChecked = enabled
            seekBar_exposure_time.isEnabled = !enabled
        } else {
            cb_auto_exposure.isEnabled = false
            toast(error)
        }
    }

    override fun onUnsupportedAutoExposure() {
        cb_auto_exposure.isEnabled = false
    }

    override fun onExposureTime(error: String?, value: Int) {
        if (error == null) {
            if (!cb_auto_exposure.isChecked) {
                seekBar_exposure_time.isEnabled = true
            }
            seekBar_exposure_time.setProgress(value.toFloat())
        } else {
            seekBar_exposure_time.isEnabled = false
            toast(error)
        }
    }

    override fun onUnsupportedExposureTime() {
        seekBar_exposure_time.isEnabled = false
    }

    override fun onExposureTimeLimit(min: Int, max: Int) {
        seekBar_exposure_time.max = max.toFloat()
        seekBar_exposure_time.min = min.toFloat()
    }

    /**
     * White balance
     */

    override fun onAutoWhiteBalance(error: String?, enabled: Boolean) {
        if (error == null) {
            cb_auto_white_balance.isEnabled = true
            cb_auto_white_balance.isChecked = enabled
            seekBar_white_balance.isEnabled = !enabled
        } else {
            cb_auto_white_balance.isEnabled = false
            toast(error)
        }
    }

    override fun onUnsupportedAutoWhiteBalance() {
        cb_auto_white_balance.isEnabled = false
    }

    override fun onWhiteBalance(error: String?, value: Int) {
        if (error == null) {
            if (!cb_auto_white_balance.isChecked) {
                seekBar_white_balance.isEnabled = true
            }
            seekBar_white_balance.setProgress(value.toFloat())
        } else {
            seekBar_white_balance.isEnabled = false
            toast(error)
        }
    }

    override fun onUnsupportedWhiteBalance() {
        seekBar_white_balance.isEnabled = false
    }

    override fun onWhiteBalanceLimit(min: Int, max: Int) {
        seekBar_white_balance.max = max.toFloat()
        seekBar_white_balance.min = min.toFloat()
    }

    /**
     * Light source
     */

    override fun onLightSource(error: String?, value: Int) {
        if (error == null) {
            tv_light_source.isEnabled = true
            rg_light_source.clearCheck()
            rg_light_source.check(value)
        } else {
            rg_light_source.removeAllViews()
            tv_light_source.isEnabled = false
            toast(error)
        }
    }

    override fun onUnsupportedLightSource() {
        tv_light_source.isEnabled = false
    }

    override fun onLightSourceLimit(min: Int, max: Int) {
        setupLightSourceRadio(min, max)
    }

    override fun onLowLightCompensation(error: String?, enabled: Boolean) {
        if (error == null) {
            cb_low_light_compensation.isEnabled = true
            cb_low_light_compensation.isChecked = enabled
        } else {
            cb_low_light_compensation.isEnabled = false
            toast(error)
        }
    }

    override fun onUnsupportedLowLightCompensation() {
        cb_low_light_compensation.isEnabled = false
    }

    private fun setupLightSourceRadio(min: Int, max: Int) {
        rg_light_source.removeAllViews()
        for (i in min..max) {
            val radioButton = RadioButton(this)
            val msg = (50 + (i - 1) * 10).toString() + "Hz"
            val dim = resources.getDimension(R.dimen.base_margin_horizontal).toInt()
            radioButton.setPadding(dim, 0, dim, 0)
            radioButton.text = msg
            radioButton.id = i // match firmware
            val layoutParams = RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(dim, 0, 0, 0)
            rg_light_source.addView(radioButton, layoutParams)
        }
    }

    private fun toast(text: String) = roUI {
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        mToast!!.show()
    }

    private val mOnCheckedListener = CompoundButton.OnCheckedChangeListener { view, isChecked ->
        when (view) {
            cb_auto_exposure -> mIPresenter.setAutoExposure(isChecked)
            cb_auto_white_balance -> mIPresenter.setAutoWhiteBalance(isChecked)
            cb_low_light_compensation -> mIPresenter.setLowLightCompensation(isChecked)
        }
    }

    private val mOnSeekChangeListener = object : OnSeekChangeListener {

        override fun onSeeking(seekParams: SeekParams?) {
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            when (seekBar) {
                seekBar_exposure_time -> mIPresenter.setExposureTime(seekBar?.progress ?: -1)
                seekBar_white_balance -> mIPresenter.setWhiteBalance(seekBar?.progress ?: -1)
            }
        }
    }

    private val mOnRadioGroupCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { _, resId ->
            if (resId >= 0) {
                mIPresenter.setLightSource(resId)
            }
        }

    private val mOnClickListener = View.OnClickListener { view ->
        when (view) {
            bt_reset -> mIPresenter.reset()
        }
    }
}