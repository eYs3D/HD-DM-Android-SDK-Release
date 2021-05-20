package com.esp.uvc.main.settings.preview_settings

import android.hardware.usb.UsbDevice
import com.esp.android.usb.camera.core.EtronCamera
import com.esp.android.usb.camera.core.StreamInfo
import com.esp.android.usb.camera.core.USBMonitor
import com.esp.android.usb.camera.core.UVCCamera
import com.esp.uvc.application.AndroidApplication
import com.esp.uvc.camera_modes.CameraModeManager
import com.esp.uvc.camera_modes.PresetMode
import com.esp.uvc.manager.KEY
import com.esp.uvc.manager.LightSourceManager
import com.esp.uvc.manager.SharedPrefManager
import com.esp.uvc.manager.VALUE
import com.esp.uvc.roi_size.RoiSize
import com.esp.uvc.roi_size.RoiSizeProvider
import com.esp.uvc.utils.*
import org.koin.core.KoinComponent
import org.koin.core.inject

const val DEFAULT_SHOW_FPS = true

const val DEFAULT_IR_EXTENDED = false
const val DEFAULT_IR_VALUE = 3

const val DEFAULT_ORIENTATION_REVERSE = true
const val DEFAULT_ORIENTATION_LANDSCAPE = false
const val DEFAULT_ORIENTATION_MIRROR = false

const val DEFAULT_PLY_FILTER = false

private val VIDEO_MODES_TABLE = mapOf(
    "COLOR_ONLY" to 0,
    "8_BITS" to 1,
    "14_BITS" to 2,
    "8_BITS_x80" to 3,
    "11_BITS" to 4,
    "OFF_RECTIFY" to 5,
    "8_BITS_RAW" to 6,
    "14_BITS_RAW" to 7,
    "8_BITS_x80_RAW" to 8,
    "11_BITS_RAW" to 9,
    "COLOR_ONLY_INTERLEAVE_MODE" to 16,
    "8_BITS_INTERLEAVE_MODE" to 17,
    "14_BITS_INTERLEAVE_MODE" to 18,
    "8_BITS_x80_INTERLEAVE_MODE" to 19,
    "11_BITS_INTERLEAVE_MODE" to 20,
    "OFF_RECTIFY_INTERLEAVE_MODE" to 21,
    "8_BITS_RAW_INTERLEAVE_MODE" to 22,
    "14_BITS_RAW_INTERLEAVE_MODE" to 23,
    "8_BITS_x80_RAW_INTERLEAVE_MODE" to 24,
    "11_BITS_RAW_INTERLEAVE_MODE" to 25
)

class MPreviewSettings(p: IPreviewSettings.Presenter, usbMonitor: USBMonitor) :
    IPreviewSettings.Model, KoinComponent {

    private val mIPresenter = p
    private val mUsbMonitor = usbMonitor

    private var mCamera: EtronCamera? = null

    private var mProductVersion: String? = null
    private var mSupportedColorRes: ArrayList<String>? = null // All of module supported
    private var mSupportedDepthRes: ArrayList<String>? = null // All of module supported
    private var mPresetModes: ArrayList<PresetMode>? = null // All of PresetMode

    private var mCurrentPresetMode: PresetMode? = null

    // UI
    private var mPresetModesString: List<String>? = null // All of PresetMode in string
    private var mCurrentVideoModes: List<String>? = null
    private var mCurrentColorRes: List<String>? = null
    private var mCurrentDepthRes: List<String>? = null
    private var mCurrentColorFps: List<String>? = null
    private var mCurrentDepthFps: List<String>? = null

    private val mRoiSizeProvider: RoiSizeProvider by inject() // Do the sharedPref self ...

    override fun getOption() {
        val showFps =
            SharedPrefManager.get(KEY.SHOW_FPS, DEFAULT_SHOW_FPS) as Boolean
        val irExtended =
            SharedPrefManager.get(KEY.IR_EXTENDED, DEFAULT_IR_EXTENDED) as Boolean
        val irCurrent =
            SharedPrefManager.get(KEY.IR_VALUE, DEFAULT_IR_VALUE) as Int
        val roiSizeList = RoiSize.values().map { it.toString() }
        val roiSizeIndex = mRoiSizeProvider.getRoiSize().ordinal
        mIPresenter.onOption(showFps, irExtended, irCurrent, roiSizeList, roiSizeIndex)
    }

    override fun getOrientation() {
        val reverse =
            SharedPrefManager.get(KEY.ORIENTATION_REVERSE, DEFAULT_ORIENTATION_REVERSE) as Boolean
        val landscape = SharedPrefManager.get(
            KEY.ORIENTATION_LANDSCAPE,
            DEFAULT_ORIENTATION_LANDSCAPE
        ) as Boolean
        val mirror =
            SharedPrefManager.get(KEY.ORIENTATION_MIRROR, DEFAULT_ORIENTATION_MIRROR) as Boolean
        val plyFilter = SharedPrefManager.get(
            KEY.PLY_FILTER,
            DEFAULT_PLY_FILTER
        ) as Boolean // Avoid much API (ply filter is not "Orientation" part)...
        mIPresenter.onOrientation(reverse, landscape, mirror, plyFilter)
    }

    override fun registerUsbMonitor() {
        mUsbMonitor.register()
    }

    override fun unregisterUsbMonitor() {
        mUsbMonitor.unregister()
    }

    override fun destroy() {
        saveInfo()
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
                mProductVersion = mCamera!!.productVersion
                mSupportedColorRes =
                    genResolution(mCamera!!.getStreamInfoList(UVCCamera.INTERFACE_NUMBER_COLOR))
                mSupportedDepthRes =
                    genResolution(mCamera!!.getStreamInfoList(UVCCamera.INTERFACE_NUMBER_DEPTH))
                val usbType = if (mCamera!!.isUSB3) VALUE.USB_TYPE_3 else VALUE.USB_TYPE_2
                if (mProductVersion!!.contains("8036")) {
                    val lowSwitch = IntArray(1)
                    mCamera!!.getFWRegisterValue(lowSwitch, 0xE5)
                    if (lowSwitch[0] == 1) {
                        mProductVersion += "_L"
                    }
                }
                mPresetModes = CameraModeManager.getPresetModes(
                    AndroidApplication.applicationContext(),
                    mCamera!!.pid,
                    usbType,
                    mProductVersion!!.contains("_L")
                ) ?: ArrayList()
                mPresetModesString = mPresetModes!!.map { it.toString() }
                mIPresenter.onModeList(mPresetModesString!!)
                if (mProductVersion == SharedPrefManager.get(KEY.PRODUCT_VERSION, "") as String &&
                    usbType == SharedPrefManager.get(KEY.USB_TYPE, "") as String
                ) {
                    preLoadPresetMode()
                } else {
                    SharedPrefManager.put(KEY.PRODUCT_VERSION, mProductVersion!!)
                    SharedPrefManager.put(KEY.USB_TYPE, usbType)
                    defaultMode()
                }
            } else {
//                mIPresenter.onConnectionFailed()
                destroy()
            }
        }
    }

    override fun onDeviceDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
    }

    override fun onModeSelected(modeIndex: Int) {
        if (mCurrentPresetMode == mPresetModes!![modeIndex]) return
        mCurrentPresetMode = mPresetModes!![modeIndex]
        updateCameraInfoList(mCurrentPresetMode!!)

        var colorResolutionIndex = -1
        var depthResolutionIndex = -1
        if (mCurrentPresetMode!!.lResolution != null) colorResolutionIndex = 0
        if (mCurrentPresetMode!!.dResolution != null) depthResolutionIndex = 0

        var colorFpsIndex = -1
        var depthFpsIndex = -1
        if (mCurrentPresetMode!!.colorFPS != null) colorFpsIndex = 0
        if (mCurrentPresetMode!!.depthFPS != null) depthFpsIndex = 0

        mIPresenter.onModeInfo(
            modeIndex,
            0,
            colorResolutionIndex != -1,
            depthResolutionIndex != -1,
            colorResolutionIndex = colorResolutionIndex,
            depthResolutionIndex = depthResolutionIndex,
            colorFpsIndex = colorFpsIndex,
            depthFpsIndex = depthFpsIndex
        )
    }

    override fun onColorFpsSelected(colorFpsIndex: Int) {
        mIPresenter.onDepthFpsInfo(colorFpsIndex)
    }

    override fun onDepthFpsSelected(depthFpsIndex: Int) {
        mIPresenter.onColorFpsInfo(depthFpsIndex)
    }

    private fun genResolution(streamInfoList: Array<StreamInfo>): ArrayList<String> {
        val resolutions = arrayListOf<String>()
        streamInfoList.forEach {
            if (it.interfaceNumber == UVCCamera.INTERFACE_NUMBER_COLOR) {
                val type = if (it.bIsFormatMJPEG) "MJPEG" else "YUYV"
                resolutions.add(String.format("%dx%d_%s", it.width, it.height, type))
            } else {
                resolutions.add(String.format("%dx%d", it.width, it.height))
            }
        }
        return resolutions
    }

    private fun genPresetVideoModes(preset: PresetMode): ArrayList<String> {
        val tmp = ArrayList<String>()
        if (preset.bits == null) {
            if (preset.description.contains("interleave mode")) {
                if (preset.rectifyMode == 1) {
                    tmp.add("OFF_RECTIFY_INTERLEAVE_MODE")
                } else {
                    tmp.add("COLOR_ONLY_INTERLEAVE_MODE")
                }
            } else {
                if (preset.rectifyMode == 1) {
                    tmp.add("OFF_RECTIFY")
                } else {
                    tmp.add("COLOR_ONLY")
                }
            }
        } else {
            if (preset.description.contains("interleave mode")) {
                preset.bits.forEach {
                    if (preset.rectifyMode == 1 || preset.lResolution == null) when (it) {
                        8 -> tmp.add("8_BITS_INTERLEAVE_MODE")
                        11 -> tmp.add("11_BITS_INTERLEAVE_MODE")
                        14 -> tmp.add("14_BITS_INTERLEAVE_MODE")
                    }
                    else when (it) {
                        8 -> tmp.add("8_BITS_RAW_INTERLEAVE_MODE")
                        11 -> tmp.add("11_BITS_RAW_INTERLEAVE_MODE")
                        14 -> tmp.add("14_BITS_RAW_INTERLEAVE_MODE")
                    }
                }
            } else {
                preset.bits.forEach {
                    if (preset.rectifyMode == 1 || preset.lResolution == null) when (it) {
                        8 -> tmp.add("8_BITS")
                        11 -> tmp.add("11_BITS")
                        14 -> tmp.add("14_BITS")
                    }
                    else when (it) {
                        8 -> tmp.add("8_BITS_RAW")
                        11 -> tmp.add("11_BITS_RAW")
                        14 -> tmp.add("14_BITS_RAW")
                    }
                }
            }
        }
        return tmp
    }

    private fun preLoadPresetMode() {
        val defaultMode = CameraModeManager.getDefaultMode(
            mCamera!!.pid,
            mCamera!!.isUSB3,
            mProductVersion!!.contains("_L")
        )
        val preLoaderPresetMode = SharedPrefManager.get(KEY.PRESET_MODE, defaultMode!!.mode) as Int
        var preLoaderVideoMode = SharedPrefManager.get(KEY.VIDEO_MODE, defaultMode.videoMode) as Int
        val preLoaderColorFps =
            SharedPrefManager.get(KEY.COLOR_FPS, defaultMode.rgbCameraState!!.fps) as Int
        val preLoaderDepthFps =
            SharedPrefManager.get(KEY.DEPTH_FPS, defaultMode.depthCameraState!!.fps) as Int
        mPresetModes!!.forEachIndexed { index, presetMode ->
            if (presetMode.mode == preLoaderPresetMode) {
                mCurrentPresetMode = presetMode
                updateCameraInfoList(mCurrentPresetMode!!)

                // Depth data type (Video mode)
                var videoModeString = ""
                val videoModeIndex: Int
                // Cover the "different design" PIF (8059/8062)
                if (mProductVersion!!.contains("YX8059") && SharedPrefManager.get(
                        KEY.INTERLEAVE_MODE,
                        false
                    ) as Boolean
                ) {
                    preLoaderVideoMode -= 16  // ex : 11_BITS_INTERLEAVE_MODE(20) -16 -> 11_BITS(4)
                }
                // Real video to String then to UI index
                VIDEO_MODES_TABLE.forEach {
                    if (it.value == preLoaderVideoMode) videoModeString = it.key
                }
                videoModeIndex = mCurrentVideoModes!!.indexOf(videoModeString)
                // Color / Depth resolution
                var colorResolutionIndex = -1
                var depthResolutionIndex = -1
                if (mCurrentPresetMode!!.lResolution != null) colorResolutionIndex = 0
                if (mCurrentPresetMode!!.dResolution != null) depthResolutionIndex = 0

                // Color / Depth fps
                var colorFpsIndex = -1
                var depthFpsIndex = -1
                if (mCurrentPresetMode!!.colorFPS != null) {
                    colorFpsIndex = mCurrentPresetMode!!.colorFPS!!.indexOf(preLoaderColorFps)
                }
                if (mCurrentPresetMode!!.depthFPS != null) {
                    depthFpsIndex = mCurrentPresetMode!!.depthFPS!!.indexOf(preLoaderDepthFps)
                }
                mIPresenter.onModeInfo(
                    index,
                    videoModeIndex,
                    colorResolutionIndex != -1,
                    depthResolutionIndex != -1,
                    colorResolutionIndex = colorResolutionIndex,
                    depthResolutionIndex = depthResolutionIndex,
                    colorFpsIndex = colorFpsIndex,
                    depthFpsIndex = depthFpsIndex
                )
            }
        }
    }

    private fun defaultMode() {
        val defaultMode = CameraModeManager.getDefaultMode(
            mCamera!!.pid,
            mCamera!!.isUSB3,
            mProductVersion!!.contains("_L")
        )
        mPresetModes!!.forEachIndexed { index, presetMode ->
            if (presetMode.mode == defaultMode!!.mode) {
                mCurrentPresetMode = presetMode
                updateCameraInfoList(mCurrentPresetMode!!)

                // Depth data type (Video mode)
                var videoModeString = ""
                // Real video to String then to UI index
                VIDEO_MODES_TABLE.forEach {
                    if (it.value == defaultMode.videoMode) videoModeString = it.key
                }
                val videoModeIndex = mCurrentVideoModes!!.indexOf(videoModeString)

                val colorFpsIndex =
                    mCurrentPresetMode!!.colorFPS!!.indexOf(defaultMode.rgbCameraState!!.fps)
                val depthFpsIndex =
                    mCurrentPresetMode!!.depthFPS!!.indexOf(defaultMode.depthCameraState!!.fps)

                mIPresenter.onModeInfo(
                    index,
                    videoModeIndex,
                    enableColor = true, // default mode always enable, please see CameraMode.kt
                    enableDepth = true, // default mode always enable, please see CameraMode.kt
                    colorResolutionIndex = 0, // Always 0 for UI index if is default mode
                    depthResolutionIndex = 0, // Always 0 for UI index if is default mode
                    colorFpsIndex = colorFpsIndex,
                    depthFpsIndex = depthFpsIndex
                )
            }
        }
    }

    // Include onDataTypeList, onColorStreamList, onDepthStreamList, onColorFpsList, onDepthFpsList
    private fun updateCameraInfoList(presetMode: PresetMode) {
        mCurrentVideoModes = genPresetVideoModes(presetMode)
        mCurrentColorRes =
            if (presetMode.lResolution == null) null else arrayListOf(presetMode.lResolution)
        mCurrentDepthRes =
            if (presetMode.dResolution == null) null else arrayListOf(presetMode.dResolution)
        mCurrentColorFps =
            if (presetMode.colorFPS == null) null else presetMode.colorFPS.map { it.toString() }
        mCurrentDepthFps =
            if (presetMode.depthFPS == null) null else presetMode.depthFPS.map { it.toString() }
        mIPresenter.onDataTypeList(mCurrentVideoModes!!)
        mIPresenter.onColorStreamList(mCurrentColorRes)
        mIPresenter.onDepthStreamList(mCurrentDepthRes)
        mIPresenter.onColorFpsList(mCurrentColorFps)
        mIPresenter.onDepthFpsList(mCurrentDepthFps)
    }

    // Only save the info when destroy
    private fun saveInfo() {
        if (mProductVersion != null) {
            saveCameraInfo()
        }
        saveOptionInfo()
        saveOtherInfo()
    }

    private fun saveCameraInfo() {
        val (presetModeIndex, videoModeIndex, enableColor, enableDepth, colorStreamIndex, depthStreamIndex, colorFpsIndex, depthFpsIndex) = mIPresenter.getCameraInfoViewValues()
        val preset = mPresetModes!![presetModeIndex as Int]
        val presetMode = preset.mode

        val videoModeString = mCurrentVideoModes!![videoModeIndex as Int]
        var videoMode = VIDEO_MODES_TABLE[videoModeString] ?: error("")

        var colorResIndex = -1
        var colorFps = -1
        if (mCurrentColorRes != null) {
            val res = mCurrentColorRes!![colorStreamIndex as Int]
            colorResIndex = mSupportedColorRes!!.indexOf(res)
            colorFps = mCurrentColorFps!![colorFpsIndex as Int].toInt()
        }

        var depthResIndex = -1
        var depthFps = -1
        if (mCurrentDepthRes != null) {
            val tmp = mCurrentDepthRes!![depthStreamIndex as Int]
            depthResIndex = mSupportedDepthRes!!.indexOf(tmp)
            depthFps = mCurrentDepthFps!![depthFpsIndex as Int].toInt()
        }

        val interleaveModeFps = preset.interLeaveModeFPS
        val isInterLeaveMode = if (interleaveModeFps == null) false else {
            interleaveModeFps == colorFps
        }

        if (isInterLeaveMode) {
            LightSourceManager.setSharedPrefs(
                LightSourceManager.INDEX_LOW_LIGHT_COMPENSATION,
                EtronCamera.LOW_LIGHT_COMPENSATION_OFF
            )
            // Cover the "different design" PIF (8059/8062)
            if (mProductVersion!!.contains("YX8059")) {
                videoMode += 16 // ex : 11_BITS(4) +16 -> 11_BITS_INTERLEAVE_MODE(20)
            }
        }

        SharedPrefManager.put(KEY.PRESET_MODE, presetMode)
        SharedPrefManager.put(KEY.VIDEO_MODE, videoMode)
        SharedPrefManager.put(KEY.ENABLE_COLOR, enableColor as Boolean)
        SharedPrefManager.put(KEY.ENABLE_DEPTH, enableDepth as Boolean)
        SharedPrefManager.put(KEY.COLOR_RESOLUTION, colorResIndex)
        SharedPrefManager.put(KEY.DEPTH_RESOLUTION, depthResIndex)
        SharedPrefManager.put(KEY.COLOR_FPS, colorFps)
        SharedPrefManager.put(KEY.DEPTH_FPS, depthFps)
        SharedPrefManager.put(KEY.INTERLEAVE_MODE, isInterLeaveMode)

        SharedPrefManager.saveAll()
    }

    private fun saveOptionInfo() {
        val (showFps, irExtended, irValue, roiSize) = mIPresenter.getOptionViewValues()
        SharedPrefManager.put(KEY.SHOW_FPS, showFps as Boolean)
        SharedPrefManager.put(KEY.IR_EXTENDED, irExtended as Boolean)
        SharedPrefManager.put(KEY.IR_VALUE, irValue as Int)
        mRoiSizeProvider.storeRoiSize(RoiSize.values()[roiSize as Int])

        SharedPrefManager.saveAll()
    }

    private fun saveOtherInfo() {
        val (isReverse, isLandscape, isMirror, isPlyFilter) = mIPresenter.getOtherViewValues()
        SharedPrefManager.put(KEY.ORIENTATION_REVERSE, isReverse as Boolean)
        SharedPrefManager.put(KEY.ORIENTATION_LANDSCAPE, isLandscape as Boolean)
        SharedPrefManager.put(KEY.ORIENTATION_MIRROR, isMirror as Boolean)
        SharedPrefManager.put(KEY.PLY_FILTER, isPlyFilter as Boolean)

        SharedPrefManager.saveAll()
    }
}