package com.esp.uvc.main.common

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.esp.android.usb.camera.core.AutoFocusCamValue
import com.esp.uvc.R
import kotlinx.android.synthetic.main.dialog_af_settings.view.*


class AutoFocusDialogFragment : DialogFragment() {

    companion object {
        const val TAG_UPDATE_REPORT = "TAG_UPDATE_REPORT"
        const val TAG_ALWAYS_CHECK = "TAG_ALWAYS_CHECK"

    }

    private lateinit var mView: View

    private var mTag = TAG_UPDATE_REPORT

    private var mAlways = false

    private var mListener: OnListener? = null

    interface OnListener {

        fun onAlwaysCheck(tag: String, always: Boolean)

        fun onClicked(tag: String)

        fun onDismiss()

    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mView = activity!!.layoutInflater.inflate(R.layout.dialog_af_settings, null)
        val builder = AlertDialog.Builder(activity!!)
        builder.setView(mView)
        setupUI()
        return builder.create()
    }

    // Full screen and bg no dim
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            if (window != null) {
                window.attributes?.gravity = Gravity.BOTTOM
                window.attributes?.dimAmount = 0f
                window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    fun setConfiguration(
        isAlways: Boolean,
        listener: OnListener
    ) {
        mAlways = isAlways
        mListener = listener
    }

    private fun setupUI() {
        mView.cb_always.isChecked = mAlways
        mView.cb_always.setOnCheckedChangeListener(mOnCheckedChangeListener)
        mView.af_update_af_report_btn.setOnClickListener(mOnClickListener)
    }

    fun getLeftCamAutoFocusROI() : AutoFocusCamValue {
        var Lcam = AutoFocusCamValue()
        Lcam.Value_AF_H_START = mView.left_cam_af_h_start?.text.toString().toInt()
        Lcam.Value_AF_V_START = mView.left_cam_af_v_start?.text.toString().toInt()
        Lcam.Value_AF_H_SIZE = mView.left_cam_af_h_size?.text.toString().toInt()
        Lcam.Value_AF_V_SIZE = mView.left_cam_af_v_size?.text.toString().toInt()
        Lcam.Value_AF_H_SKIP = mView.left_cam_af_h_skip?.text.toString().toInt()
        Lcam.Value_AF_V_SKIP = mView.left_cam_af_v_skip?.text.toString().toInt()
        Lcam.Value_AF_THD = mView.left_cam_af_thd?.text.toString().toInt()
        return Lcam
    }

    fun getRightCamAutoFocusROI() : AutoFocusCamValue {
        var Rcam = AutoFocusCamValue()
        Rcam.Value_AF_H_START = mView.right_cam_af_h_start?.text.toString().toInt()
        Rcam.Value_AF_V_START = mView.right_cam_af_v_start?.text.toString().toInt()
        Rcam.Value_AF_H_SIZE = mView.right_cam_af_h_size?.text.toString().toInt()
        Rcam.Value_AF_V_SIZE = mView.right_cam_af_v_size?.text.toString().toInt()
        Rcam.Value_AF_H_SKIP = mView.right_cam_af_h_skip?.text.toString().toInt()
        Rcam.Value_AF_V_SKIP = mView.right_cam_af_v_skip?.text.toString().toInt()
        Rcam.Value_AF_THD = mView.right_cam_af_thd?.text.toString().toInt()
        return Rcam
    }

    fun updateAutoFocusReport(LCam: AutoFocusCamValue , RCam: AutoFocusCamValue) {
        mView.left_cam_af_report?.setText(LCam.Value_AF_REPORT.toString())
        mView.right_cam_af_report?.setText(RCam.Value_AF_REPORT.toString())
    }

    private val mOnCheckedChangeListener =  CompoundButton.OnCheckedChangeListener { view, isChecked ->
        when (view) {
            mView.cb_always -> mTag = TAG_ALWAYS_CHECK
        }
        mAlways = isChecked
        mListener?.onAlwaysCheck(mTag, isChecked)
    }

    private val mOnClickListener = View.OnClickListener { p0 ->
        when (p0!!.id) {
            R.id.af_update_af_report_btn -> mListener?.onClicked(TAG_UPDATE_REPORT)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mListener?.onDismiss()
    }
}