package com.esp.uvc.main.settings.preview_settings

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.uvc.BaseFragment
import com.esp.uvc.R
import com.esp.uvc.utils.logd
import com.esp.uvc.utils.roUI
import kotlinx.android.synthetic.main.activity_preview_image_settings.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val ADAPTER_INDEX_PRESET = 0
const val ADAPTER_INDEX_DATA_TYPE = 1
const val ADAPTER_INDEX_COLOR_STREAM = 2
const val ADAPTER_INDEX_DEPTH_STREAM = 3
const val ADAPTER_INDEX_COLOR_FPS = 4
const val ADAPTER_INDEX_DEPTH_FPS = 5

class PreviewSettingsActivity : AppCompatActivity(), IPreviewSettings.View {

    companion object {
        fun startInstance(context: Context) {
            context.startActivity(Intent(context, PreviewSettingsActivity::class.java))
        }
    }

    private val mIPresenter: IPreviewSettings.Presenter by inject {
        parametersOf(
            this,
            USBMonitor(this, mOnDeviceConnectListener)
        )
    }

    private var mAdapters = arrayOfNulls<ArrayAdapter<String>>(6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        BaseFragment.applyScreenRotation(this)
        setContentView(R.layout.activity_preview_image_settings)
        initUI()
    }

    override fun onStart() {
        super.onStart()
        mIPresenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        mIPresenter.onStop()
    }

    override fun onOption(
        showFps: Boolean,
        /* remove IR UI from settings
        irExtended: Boolean,
        irCurrent: Int,
        */
        roiSizeList: List<String>,
        roiSizeIndex: Int
    ) = roUI {
        cb_show_fps.isChecked = showFps
        /* remove IR UI from settings
        cb_ir_extended.isChecked = irExtended
        seekBar_ir.max = if (irExtended) 15f else 6f
        seekBar_ir.min = 0.0f
        seekBar_ir.tickCount = (seekBar_ir.max - seekBar_ir.min + 1).toInt()
        seekBar_ir.setProgress(irCurrent.toFloat())
        */
        seekBar_roi.max = roiSizeList.size.toFloat() - 1
        seekBar_roi.tickCount = roiSizeList.size
        seekBar_roi.customTickTexts(roiSizeList.toTypedArray())
        seekBar_roi.setProgress(roiSizeIndex.toFloat())
    }

    override fun onOrientation(
        reverse: Boolean,
        landscape: Boolean,
        mirror: Boolean,
        plyFilter: Boolean
    ) = roUI {
        cb_orientation_reverse.isChecked = reverse
        cb_orientation_landscape.isChecked = landscape
        cb_mirror_mode.isChecked = mirror
        cb_ply_filter.isChecked = plyFilter
    }

    override fun onModeList(list: List<String>) = roUI {
        mAdapters[ADAPTER_INDEX_PRESET]!!.clear()
        mAdapters[ADAPTER_INDEX_PRESET]!!.addAll(list)
    }

    override fun onDataTypeList(list: List<String>) = roUI {
        mAdapters[ADAPTER_INDEX_DATA_TYPE]!!.clear()
        mAdapters[ADAPTER_INDEX_DATA_TYPE]!!.addAll(list)
    }

    override fun onColorStreamList(list: List<String>?) = roUI {
        mAdapters[ADAPTER_INDEX_COLOR_STREAM]!!.clear()
        if (list != null) mAdapters[ADAPTER_INDEX_COLOR_STREAM]!!.addAll(list)
    }

    override fun onDepthStreamList(list: List<String>?) = roUI {
        mAdapters[ADAPTER_INDEX_DEPTH_STREAM]!!.clear()
        if (list != null) mAdapters[ADAPTER_INDEX_DEPTH_STREAM]!!.addAll(list)
    }

    override fun onColorFpsList(list: List<String>?) = roUI {
        mAdapters[ADAPTER_INDEX_COLOR_FPS]!!.clear()
        if (list != null) mAdapters[ADAPTER_INDEX_COLOR_FPS]!!.addAll(list)
    }

    override fun onDepthFpsList(list: List<String>?) = roUI {
        mAdapters[ADAPTER_INDEX_DEPTH_FPS]!!.clear()
        if (list != null) mAdapters[ADAPTER_INDEX_DEPTH_FPS]!!.addAll(list)
    }

    override fun getCameraInfoViewValues(): ArrayList<Any?> {
        val presetModeIndex = spinner_preset.selectedItemPosition
        val videoModeIndex = spinner_depth_data_type.selectedItemPosition
        val enableColor = cb_enable_color_stream.isChecked
        val enableDepth = cb_enable_depth_stream.isChecked
        val colorStreamIndex = spinner_color_stream.selectedItemPosition
        val depthStreamIndex = spinner_depth_stream.selectedItemPosition
        val colorFpsIndex = spinner_color_fps.selectedItemPosition
        val depthFpsIndex = spinner_depth_fps.selectedItemPosition
        return arrayListOf(
            presetModeIndex,
            videoModeIndex,
            enableColor,
            enableDepth,
            colorStreamIndex,
            depthStreamIndex,
            colorFpsIndex,
            depthFpsIndex
        )
    }

    override fun getOptionViewValues(): ArrayList<Any?> {
        val showFps = cb_show_fps.isChecked
        /* remove IR UI from settings
        val irExtended = cb_ir_extended.isChecked
        val irValue = seekBar_ir.progress
        */
        val roiSize = seekBar_roi.progress
        return arrayListOf(showFps, /*irExtended, irValue,*/ roiSize)//remove IR UI from settings
    }

    override fun getOtherViewValues(): ArrayList<Any?> {
        val isReverse = cb_orientation_reverse.isChecked
        val isLandscape = cb_orientation_landscape.isChecked
        val isMirror = cb_mirror_mode.isChecked
        val isPlyFilter = cb_ply_filter.isChecked
        return arrayListOf(isReverse, isLandscape, isMirror, isPlyFilter)
    }

    override fun onModeInfo(
        modeIndex: Int,
        depthDataTypeIndex: Int,
        enableColor: Boolean,
        enableDepth: Boolean,
        colorResolutionIndex: Int,
        depthResolutionIndex: Int,
        colorFpsIndex: Int,
        depthFpsIndex: Int
    ) = roUI {
        spinner_preset.setSelection(modeIndex)
        spinner_depth_data_type.setSelection(depthDataTypeIndex)
        cb_enable_color_stream.isChecked = enableColor
        cb_enable_depth_stream.isChecked = enableDepth
        spinner_color_stream.setSelection(colorResolutionIndex)
        spinner_depth_stream.setSelection(depthResolutionIndex)
        layout_color_fps_manual.visibility = View.GONE
        layout_depth_fps_manual.visibility = View.GONE
        layout_color_fps_preset.visibility = View.VISIBLE
        layout_depth_fps_preset.visibility = View.VISIBLE
        spinner_color_fps.setSelection(colorFpsIndex)
        spinner_depth_fps.setSelection(depthFpsIndex)

        cb_enable_color_stream.isEnabled = false
        cb_enable_depth_stream.isEnabled = false
        spinner_color_stream.isEnabled = false
        spinner_depth_stream.isEnabled = false
    }

    override fun onColorFpsInfo(index: Int) = roUI {
        spinner_color_fps.setSelection(index)
    }

    override fun onDepthFpsInfo(index: Int) = roUI {
        spinner_depth_fps.setSelection(index)
    }

    private fun initUI() {
        for (i in mAdapters.indices) {
            mAdapters[i] = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                ArrayList<String>()
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }
        spinner_preset.adapter = mAdapters[ADAPTER_INDEX_PRESET]
        spinner_preset.onItemSelectedListener = mOnItemSelectedListener
        spinner_depth_data_type.adapter = mAdapters[ADAPTER_INDEX_DATA_TYPE]
        spinner_color_stream.adapter = mAdapters[ADAPTER_INDEX_COLOR_STREAM]
        spinner_depth_stream.adapter = mAdapters[ADAPTER_INDEX_DEPTH_STREAM]
        spinner_color_fps.adapter = mAdapters[ADAPTER_INDEX_COLOR_FPS]
        spinner_color_fps.onItemSelectedListener = mOnItemSelectedListener
        spinner_depth_fps.adapter = mAdapters[ADAPTER_INDEX_DEPTH_FPS]
        spinner_depth_fps.onItemSelectedListener = mOnItemSelectedListener
        //cb_ir_extended.setOnCheckedChangeListener(mOnCheckedListener)//remove IR UI from settings
    }

    private val mOnItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            when (p0!!) {
                spinner_preset -> {
                    mIPresenter.onModeSelected(p2)
                }
                spinner_color_fps -> {
                    mIPresenter.onColorFpsSelected(p2)
                }
                spinner_depth_fps -> {
                    mIPresenter.onDepthFpsSelected(p2)
                }
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
        }
    }
    /* remove IR UI from settings
    private val mOnCheckedListener = CompoundButton.OnCheckedChangeListener { view, isChecked ->
        when (view) {
            cb_ir_extended -> {
                seekBar_ir.max = if (isChecked) 15f else 6f
                seekBar_ir.min = 0.0f
                seekBar_ir.tickCount = (seekBar_ir.max - seekBar_ir.min + 1).toInt()
                seekBar_ir.setProgress(
                    seekBar_ir.progressFloat.coerceIn(
                        seekBar_ir.min,
                        seekBar_ir.max
                    )
                )
            }
        }
    }
    */

    private val mOnDeviceConnectListener = object : USBMonitor.OnDeviceConnectListener {

        override fun onAttach(device: UsbDevice?) {
            mIPresenter.onDeviceAttach(device)
        }

        override fun onDetach(device: UsbDevice?) {
            mIPresenter.onDeviceDetach(device)
        }

        override fun onConnect(
            device: UsbDevice?,
            ctrlBlock: USBMonitor.UsbControlBlock?,
            createNew: Boolean
        ) {
            mIPresenter.onDeviceConnect(device, ctrlBlock, createNew)
        }

        override fun onDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
            mIPresenter.onDeviceDisconnect(device, ctrlBlock)
        }

        override fun onCancel() {
        }
    }
}