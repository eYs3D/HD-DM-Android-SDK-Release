package com.esp.uvc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.fragment.app.Fragment
import com.esp.uvc.manager.KEY
import com.esp.uvc.manager.SharedPrefManager

/**
 * A Base Fragment that is aware of all the possible state of a particular screen
 * */
abstract class BaseFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        activity?.let {
            applyScreenRotation(it)
        }
    }

    companion object {
        /**
         * Helper method that forces a screen rotation for an activity
         * */
        @SuppressLint("SourceLockedOrientationActivity")
        fun applyScreenRotation(activity: Activity) {
            val mUpsideDownMode = SharedPrefManager.get(KEY.ORIENTATION_REVERSE, true) as Boolean
            if (mUpsideDownMode) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            } else {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
}