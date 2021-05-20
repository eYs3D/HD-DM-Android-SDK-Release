package com.esp.uvc.manager

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import com.esp.uvc.application.AndroidApplication

object SharedPrefManager {

    private val mSP =
        PreferenceManager.getDefaultSharedPreferences(AndroidApplication.applicationContext())

    private val mMaps = mutableMapOf<String, Any>()

    fun put(key: String, value: Any) {
        mMaps[key] = value
    }

    fun get(key: String, default: Any): Any {
        return when (default) {
            is String -> mSP.getString(key, default)
            is Int -> mSP.getInt(key, default)
            is Float -> mSP.getFloat(key, default)
            is Long -> mSP.getLong(key, default)
            is Boolean -> mSP.getBoolean(key, default)
            else -> throw IllegalAccessException("SharedPrefManager get exception")
        }
    }

    fun saveAll() {
        val editor = mSP.edit()
        mMaps.forEach {
            val key = it.key
            val value = it.value
            when (it.value) {
                is String -> editor.putString(key, value as String)
                is Int -> editor.putInt(key, value as Int)
                is Float -> editor.putFloat(key, value as Float)
                is Long -> editor.putLong(key, value as Long)
                is Boolean -> editor.putBoolean(key, value as Boolean)
                else -> editor.putString(key, value as String)
            }
        }
        editor.commit()
        mMaps.clear()
    }

    @SuppressLint("CommitPrefEdits")
    fun clearAll() {
        val editor = mSP.edit()
        editor.clear()
        editor.commit()
        mMaps.clear()
    }
}

object KEY {

    const val PRODUCT_VERSION =
        "KEY_PRODUCT_VERSION" // For compare the module whether is same as "CameraFragment"

    const val USB_TYPE =
        "KEY_USB_TYPE" // For compare the module whether is same as "CameraFragment"

    const val PRESET_MODE = "KEY_PRESET_MODE" // Real mode, see the 8037 csv, ex : 31, 32, 33 ...

    const val VIDEO_MODE = "KEY_VIDEO_MODE" // Real video mode (set to firmware)

    const val ENABLE_COLOR = "KEY_ENABLE_COLOR"
    const val ENABLE_DEPTH = "KEY_ENABLE_DEPTH"

    const val COLOR_RESOLUTION = "KEY_COLOR_RESOLUTION" // Index of module supported list
    const val DEPTH_RESOLUTION = "KEY_DEPTH_RESOLUTION" // Index of module supported list

    const val COLOR_FPS = "KEY_COLOR_FPS"
    const val DEPTH_FPS = "KEY_DEPTH_FPS"

    const val INTERLEAVE_MODE = "KEY_INTERLEAVE_MODE"

    const val SHOW_FPS = "KEY_SHOW_FPS"

    const val IR_EXTENDED = "KEY_IR_EXTENDED"
    const val IR_VALUE = "KEY_IR_VALUE"
    const val IR_MIN = "KEY_IR_MIN"
    const val IR_MAX = "KEY_IR_MAX"

    const val ORIENTATION_REVERSE = "KEY_ORIENTATION_REVERSE"
    const val ORIENTATION_LANDSCAPE = "KEY_ORIENTATION_LANDSCAPE"
    const val ORIENTATION_MIRROR = "KEY_ORIENTATION_MIRROR"

    const val PLY_FILTER = "KEY_PLY_FILTER"

    const val AUTO_EXPOSURE = "KEY_AUTO_EXPOSURE"
    const val EXPOSURE_ABSOLUTE_TIME = "KEY_EXPOSURE_ABSOLUTE_TIME"

    const val AUTO_WHITE_BALANCE = "KEY_AUTO_WHITE_BALANCE"
    const val CURRENT_WHITE_BALANCE = "KEY_CURRENT_WHITE_BALANCE"
    const val MIN_WHITE_BALANCE = "KEY_MIN_WHITE_BALANCE"
    const val MAX_WHITE_BALANCE = "KEY_MAX_WHITE_BALANCE"

    const val CURRENT_LIGHT_SOURCE = "KEY_CURRENT_LIGHT_SOURCE"
    const val MIN_LIGHT_SOURCE = "KEY_MIN_LIGHT_SOURCE"
    const val MAX_LIGHT_SOURCE = "KEY_MAX_LIGHT_SOURCE"
    const val LOW_LIGHT_COMPENSATION = "KEY_LOW_LIGHT_COMPENSATION"
}

object VALUE {

    const val USB_TYPE_2 = "2"
    const val USB_TYPE_3 = "3"
}