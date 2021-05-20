package com.esp.uvc.main.settings

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.esp.uvc.BuildConfig
import com.esp.uvc.R
import kotlinx.android.synthetic.main.dialog_software_version.view.*

class SoftwareVersionDialogFragment(private val camera: String? = null) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_software_version, null)
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(R.string.software_version_dialog_title)
        builder.setView(view)
        builder.setPositiveButton("OK") { _, _ -> dismiss() }
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.black))
        }

        view.tv_app_version.text = resources.getString(
            R.string.software_version_dialog_app_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.BUILD_NUMBER
        )
        view.tv_sdk_version.text = resources.getString(
            R.string.software_version_dialog_sdk_version,
            com.esp.android.usb.camera.core.BuildConfig.VERSION_NAME
        )
        view.tv_license.text =
            resources.getString(R.string.software_version_dialog_licenses, "Apache 2.0, MIT")
        view.tv_copyright.text = resources.getString(
            R.string.software_version_dialog_copyright,
            BuildConfig.BUILD_YEAR,
            "eYs3D Microelectronics"
        )

        if (camera != null && camera.isNotEmpty()) {
            view.tv_camera.text =
                resources.getString(R.string.software_version_dialog_connected_camera, camera)
        } else {
            view.tv_camera.visibility = View.GONE
        }

        return dialog
    }
}