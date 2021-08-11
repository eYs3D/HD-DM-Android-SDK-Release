package com.esp.uvc.manager

import android.content.Context
import androidx.annotation.NonNull
import com.esp.android.usb.camera.core.ApcCamera
import com.esp.android.usb.camera.core.ApcCamera.*
import com.esp.uvc.utils.loge
import com.esp.uvc.utils.logi

class IRManager(context: Context) {

    private val DEBUG_IR = false
    private val IR_UNINITIALIZED_VALUE = -2
    private val IR_MIN_VALUE = 0
    private val IR_DEF_VALUE = 3
    private val IR_DEF_MAX_VALUE = 6
    private val IR_EXT_MAX_VALUE = 15
    private val IRMAX_NOT_SUP_CTRL = 0xff

    var irMin = IR_UNINITIALIZED_VALUE
    var irMax = IR_UNINITIALIZED_VALUE
    var irCurrentVal = IR_UNINITIALIZED_VALUE
    var irExtendedFlag = false

    /**
     * Return if setting firmware registers successfully
     */
    fun setIrCurrentVal(apcCamera: ApcCamera?, value: Int): Boolean {
        return if (apcCamera?.setIRCurrentValue(value) != EYS_ERROR) {
            logi("[ir_ext] IRManager setIrCurrentVal $value")
            irCurrentVal = value
            dump()
            true
        } else {
            logi("[ir_ext] IRManager setIrCurrentVal failed $value")
            dump()
            false
        }
    }

    /**
     * Return if setting firmware registers successfully
     */
    fun setIRExtension(apcCamera: ApcCamera?, isEnable: Boolean): Boolean {
        if (apcCamera == null) {
            loge("[ir_ext] err camera null failing setting ir extension")
            dump()
            return false
        }

        val currentIrMax = apcCamera.irMaxValue
        if (currentIrMax != IRMAX_NOT_SUP_CTRL) {
            val result: Int = if (isEnable) {
                loge("[ir_ext] setIRExtension 15")
                apcCamera.setIRMaxValue(IR_EXT_MAX_VALUE)
            } else {
                loge("[ir_ext] setIRExtension 6")
                apcCamera.setIRMaxValue(IR_DEF_MAX_VALUE)
            }

            if (result != EYS_ERROR) {
                irMax = if (isEnable) IR_EXT_MAX_VALUE
                else IR_DEF_MAX_VALUE
                irExtendedFlag = isEnable
                dump()
                return true
            } else {
                loge("[ir_ext] err camera setIRExtension EYS_ERROR")
                dump()
                return false
            }
        }
        loge("[ir_ext] err camera not support IR control")
        dump()
        return false
    }

    fun initIR(@NonNull apcCamera: ApcCamera, isFirstLaunch: Boolean) {
        loge("[ir_ext] initIR firstLaunch $isFirstLaunch")
        if (isFirstLaunch) {
            setIrCurrentVal(apcCamera, IR_DEF_VALUE)
            if (!setIRExtension(apcCamera, false)) {
                loge("[ir_ext] startCameraViaDefaults set false failed")
            }
            irMin = apcCamera.irMinValue
            irMax = apcCamera.irMaxValue
            dump()
            return
        }

        readIRFromSharePref(apcCamera)
        dump()
    }

    fun clear() {
        irMin = IR_UNINITIALIZED_VALUE
        irMax = IR_UNINITIALIZED_VALUE
        irCurrentVal = IR_UNINITIALIZED_VALUE
        irExtendedFlag = false
        loge("[ir_ext] cleared!!")
    }

    private fun readIRFromSharePref(apcCamera: ApcCamera?) {
        loge("[ir_ext] readIRFromSharePref")
        val isIRExtendSharedPref = SharedPrefManager.get(KEY.IR_EXTENDED, irExtendedFlag) as Boolean
        setIRExtension(apcCamera, isIRExtendSharedPref)

        val irValueSharedPref = SharedPrefManager.get(KEY.IR_VALUE, irCurrentVal) as Int
        if (irCurrentVal != irValueSharedPref) {
            setIrCurrentVal(apcCamera, irValueSharedPref)
        }

        irMin = SharedPrefManager.get(KEY.IR_MIN, irMin) as Int
        dump()
    }

    fun saveSharedPref() {
        SharedPrefManager.put(KEY.IR_VALUE, irCurrentVal)
        SharedPrefManager.put(KEY.IR_MIN, irMin)
        SharedPrefManager.put(KEY.IR_MAX, irMax)
        SharedPrefManager.put(KEY.IR_EXTENDED, irExtendedFlag)
        SharedPrefManager.saveAll()
        loge("[ir_ext] IR_EXTENDED @0 $irExtendedFlag")
        dump()
    }

    fun dump() {
        if (!DEBUG_IR) return
        if (irMin < 0 || irMax < 0 || irCurrentVal < 0) {
            loge("[ir_ext] dump err $irExtendedFlag")
        }
        loge("[ir_ext] IR: $irMin, $irMax, $irCurrentVal, $irExtendedFlag")
    }

    fun validIR(): Boolean {
        return irMax >= 0 && irMin >= 0 && irMax >= irMin && irCurrentVal in irMin..irMax
    }

}
